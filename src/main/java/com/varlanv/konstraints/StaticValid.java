package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

@NotNullByDefault
public interface StaticValid<SUBJECT> {

    <NEXT_SUBJECT> StaticValid<NEXT_SUBJECT> map(Function<SUBJECT, NEXT_SUBJECT> mapper);

    <NEXT_SUBJECT> StaticValid<NEXT_SUBJECT> flatMap(Function<SUBJECT, StaticValid<NEXT_SUBJECT>> mapper);

    SUBJECT orElseThrow(Supplier<? extends Throwable> exceptionSupplier);

    SUBJECT orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn);

    Optional<SUBJECT> optional();

    boolean isValid();

    boolean isNotValid();

    @Unmodifiable
    List<Violation> violations();

    static <SUBJECT> StaticValid<SUBJECT> ofValid(SUBJECT value) {
        Objects.requireNonNull(value);
        return new AlwaysValid<>(() -> value);
    }

    static <SUBJECT> StaticValid<SUBJECT> ofValid(Supplier<@NotNull SUBJECT> supplier) {
        return new AlwaysValid<>(Objects.requireNonNull(supplier));
    }

    static <SUBJECT> StaticValid<SUBJECT> ofInvalid(List<Violation> violations) {
        if (violations.isEmpty()) {
            throw new IllegalArgumentException("Violations must not be empty");
        }
        return new Invalid<>(List.copyOf(violations));
    }

    static <SUBJECT> ValidationSpec<SUBJECT> validationSpec(Function<AssertionsChain<SUBJECT, SUBJECT>, AssertionsChain<SUBJECT, ?>> specAction) {
        return ValidationSpec.fromRules(
                specAction.apply(
                        new AssertionsChain<>(
                                Rules.create(),
                                Function.identity()
                        )
                ).rules()
        );
    }

    interface Rules<SUBJECT> {

        Rules<SUBJECT> add(Rule<SUBJECT> rule);

        @Unmodifiable
        List<Violation> apply(SUBJECT t);

        static <SUBJECT> Rules<SUBJECT> create() {
            var rules = new ArrayList<Rule<SUBJECT>>();
            return new Rules<>() {

                @Override
                public Rules<SUBJECT> add(Rule<SUBJECT> rule) {
                    rules.add(Objects.requireNonNull(rule));
                    return this;
                }

                @Override
                public List<Violation> apply(SUBJECT t) {
                    List<Violation> violations = null;
                    for (var rule : rules) {
                        var maybeViolation = rule.apply(t);
                        if (maybeViolation.isPresent()) {
                            if (violations == null) {
                                violations = new ArrayList<>(5);
                            }
                            violations.add(maybeViolation.get());
                        }
                    }
                    return violations == null ? List.of() : Collections.unmodifiableList(violations);
                }
            };
        }
    }

    interface ValidationSpec<SUBJECT> {

        Function<SUBJECT, StaticValid<SUBJECT>> toValidationFunction();

        UnaryOperator<SUBJECT> toValidationFunction(Function<List<Violation>, ? extends Throwable> onException);

        UnaryOperator<SUBJECT> toValidationFunction(Supplier<? extends Throwable> onException);

        StaticValid<SUBJECT> validate(SUBJECT t);

        static <T> ValidationSpec<T> fromRules(Rules<T> rules) {
            Objects.requireNonNull(rules);
            return new ValidationSpec<>() {

                @Override
                public Function<T, StaticValid<T>> toValidationFunction() {
                    return this::validate;
                }

                @Override
                public UnaryOperator<T> toValidationFunction(Function<List<Violation>, ? extends Throwable> onException) {
                    Objects.requireNonNull(onException);
                    return t -> validate(t).orElseThrow(onException);
                }

                @Override
                public UnaryOperator<T> toValidationFunction(Supplier<? extends Throwable> onException) {
                    Objects.requireNonNull(onException);
                    return t -> validate(t).orElseThrow(onException);
                }

                @Override
                public StaticValid<T> validate(T t) {
                    Objects.requireNonNull(t);
                    var violations = rules.apply(t);
                    if (violations.isEmpty()) {
                        return StaticValid.ofValid(t);
                    } else {
                        return new Invalid<>(violations);
                    }
                }
            };
        }
    }

    interface Rule<SUBJECT> extends Function<@NotNull SUBJECT, @NotNull Optional<Violation>> {
    }

    final class AssertionsChain<PREVIOUS, CURRENT> {

        private final Rules<PREVIOUS> rules;
        private final Function<PREVIOUS, CURRENT> currentNestFn;

        public AssertionsChain(Rules<PREVIOUS> rules, Function<PREVIOUS, CURRENT> currentNestFn) {
            this.rules = rules;
            this.currentNestFn = currentNestFn;
        }

        public <NEXT> AssertionsChain<PREVIOUS, NEXT> nested(String fieldName, Function<@NotNull CURRENT, @Nullable NEXT> valueFn) {
            return new AssertionsChain<>(rules, (PREVIOUS t) -> valueFn.apply(currentNestFn.apply(t)));
        }

        public AssertionsChain<PREVIOUS, CURRENT> customValidation(String message, Function<CURRENT, Boolean> valueFn) {
            rules.add(t -> {
                if (!valueFn.apply(currentNestFn.apply(t))) {
                    return Optional.of(Violation.of(message));
                } else {
                    return Optional.empty();
                }
            });
            return this;
        }

        public AssertionsChain<PREVIOUS, CURRENT> isNull(String fieldName, Function<@NotNull CURRENT, @Nullable Object> valueFn) {
            rules.add(t -> {
                if (valueFn.apply(currentNestFn.apply(t)) != null) {
                    return Optional.of(Violation.of(fieldName, "Field [%s] is expected to be null".formatted(fieldName)));
                } else {
                    return Optional.empty();
                }
            });
            return this;
        }

        public AssertionsChain<PREVIOUS, CURRENT> assertNotNull(String fieldName, Function<@NotNull CURRENT, @Nullable Object> valueFn) {
            rules.add(t -> {
                if (valueFn.apply(currentNestFn.apply(t)) == null) {
                    return Optional.of(Violation.of(fieldName, "Field [%s] is expected to be non-null".formatted(fieldName)));
                } else {
                    return Optional.empty();
                }
            });
            return this;
        }

        public StringAssertions<PREVIOUS, CURRENT> stringField(String fieldName, Function<@NotNull CURRENT, @Nullable String> stringValueFn) {
            Function<CURRENT, Optional<String>> fn = wrapOptional(stringValueFn);
            return new StringAssertions<>(currentNestFn, fn, () -> fieldName, this);
        }

        public NumberAssertions<Integer, PREVIOUS, CURRENT> numberField(String fieldName, Function<@NotNull CURRENT, @Nullable Integer> integerValueFn) {
            Function<CURRENT, Optional<Integer>> fn = wrapOptional(integerValueFn);
            return new NumberAssertions<>(currentNestFn, fn, Integer::compareTo, () -> fieldName, this);
        }

        AssertionsChain<PREVIOUS, CURRENT> withRule(Rule<PREVIOUS> rule) {
            rules.add(rule);
            return this;
        }

        Rules<PREVIOUS> rules() {
            return rules;
        }
    }

    final class NumberAssertions<TARGET extends Number, PREVIOUS, CURRENT> {

        private final Function<PREVIOUS, CURRENT> currentNestFn;
        private final Function<@NotNull CURRENT, @NotNull Optional<TARGET>> numberValueFn;
        private final Comparator<TARGET> comparator;
        private final Supplier<String> fieldName;
        private final AssertionsChain<PREVIOUS, CURRENT> parent;

        public NumberAssertions(Function<PREVIOUS, CURRENT> currentNestFn,
                                Function<@NotNull CURRENT, @NotNull Optional<TARGET>> numberValueFn,
                                Comparator<TARGET> comparator,
                                Supplier<String> fieldName,
                                AssertionsChain<PREVIOUS, CURRENT> parent) {
            this.currentNestFn = currentNestFn;
            this.numberValueFn = numberValueFn;
            this.comparator = comparator;
            this.fieldName = fieldName;
            this.parent = parent;
        }

        public NullAssertions<TARGET, PREVIOUS, CURRENT> isGte(TARGET target) {
            Objects.requireNonNull(target);
            return new NullAssertions<>(
                    currentNestFn,
                    numberValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or number greater than or equal to [%d]" :
                            "Field [%s] must be not null and greater than or equal to [%d]").formatted(fieldName, target),
                    val -> comparator.compare(val, target) >= 0,
                    parent
            );
        }

        public NullAssertions<TARGET, PREVIOUS, CURRENT> isLte(TARGET target) {
            Objects.requireNonNull(target);
            return new NullAssertions<>(
                    currentNestFn,
                    numberValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or number less than [%d]" :
                            "Field [%s] must be not null and less than [%d]").formatted(fieldName, target),
                    val -> comparator.compare(val, target) <= 0,
                    parent
            );
        }

        public NullAssertions<TARGET, PREVIOUS, CURRENT> isInRange(TARGET minTarget, TARGET maxTarget) {
            if (comparator.compare(minTarget, maxTarget) > 0) {
                throw new IllegalArgumentException("minTarget must be less than maxTarget");
            }
            return new NullAssertions<>(
                    currentNestFn,
                    numberValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or integer in range [%d - %d]" :
                            "Field [%s] must be not null and in range [%d - %d]").formatted(fieldName, minTarget, maxTarget),
                    val -> comparator.compare(val, minTarget) >= 0 && comparator.compare(val, maxTarget) <= 0,
                    parent
            );
        }
    }

    final class StringAssertions<PREVIOUS, CURRENT> {

        private final Function<PREVIOUS, CURRENT> currentNestFn;
        private final Function<@NotNull CURRENT, @NotNull Optional<String>> stringValueFn;
        private final Supplier<String> fieldName;
        private final AssertionsChain<PREVIOUS, CURRENT> parent;

        public StringAssertions(Function<PREVIOUS, CURRENT> currentNestFn,
                                Function<@NotNull CURRENT, @NotNull Optional<String>> stringValueFn,
                                Supplier<String> fieldName,
                                AssertionsChain<PREVIOUS, CURRENT> parent) {
            this.currentNestFn = currentNestFn;
            this.stringValueFn = stringValueFn;
            this.fieldName = fieldName;
            this.parent = parent;
        }

        public NullAssertions<String, PREVIOUS, CURRENT> isEmpty() {
            return new NullAssertions<>(
                    currentNestFn,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or empty string" :
                            "Field [%s] must be not null and empty string").formatted(fieldName),
                    String::isEmpty,
                    parent
            );
        }

        public NullAssertions<String, PREVIOUS, CURRENT> isNotBlank() {
            return new NullAssertions<>(
                    currentNestFn,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or non-blank string" :
                            "Field [%s] must be not null and non-blank string").formatted(fieldName),
                    val -> !val.isBlank(),
                    parent
            );
        }

        public NullAssertions<String, PREVIOUS, CURRENT> assertLength(Integer length) {
            Objects.requireNonNull(length);
            return new NullAssertions<>(
                    currentNestFn,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or string with length [%d]" :
                            "Field [%s] must be not null and have length [%d]").formatted(fieldName, length),
                    val -> val.length() == length,
                    parent
            );
        }

        public NullAssertions<String, PREVIOUS, CURRENT> hasMinLength(Integer minLength) {
            Objects.requireNonNull(minLength);
            return new NullAssertions<>(
                    currentNestFn,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or string with min length [%d]" :
                            "Field [%s] must be not null and have min length [%d]").formatted(fieldName, minLength)
                            .formatted(minLength),
                    val -> val.length() >= minLength,
                    parent
            );
        }

        public NullAssertions<String, PREVIOUS, CURRENT> hasMaxLength(Integer maxLength) {
            Objects.requireNonNull(maxLength);
            return new NullAssertions<>(
                    currentNestFn,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or string with max length [%d]" :
                            "Field [%s] must be not null and have max length [%d]").formatted(fieldName, maxLength),
                    val -> val.length() <= maxLength,
                    parent
            );
        }

        public NullAssertions<String, PREVIOUS, CURRENT> hasLengthRange(Integer minLength, Integer maxLength) {
            Objects.requireNonNull(minLength);
            Objects.requireNonNull(maxLength);
            return new NullAssertions<>(
                    currentNestFn,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or string with length range [%d - %d]" :
                            "Field [%s] must be not null and have length range [%d - %d]").formatted(fieldName, minLength, maxLength),
                    val -> val.length() >= minLength && val.length() <= maxLength,
                    parent
            );
        }

        public NullAssertions<String, PREVIOUS, CURRENT> matches(Pattern pattern) {
            Objects.requireNonNull(pattern);
            return new NullAssertions<>(
                    currentNestFn,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or string that matches pattern [%s]" :
                            "Field [%s] must be not null and match pattern [%s]").formatted(fieldName, pattern.pattern()),
                    val -> pattern.matcher(val).matches(),
                    parent
            );
        }
    }

    final class NullAssertions<TARGET, PREVIOUS, CURRENT> {

        private final Function<PREVIOUS, CURRENT> currentNestFn;
        private final Function<@NotNull CURRENT, @NotNull Optional<TARGET>> valueFn;
        private final Supplier<String> fieldNameSupplier;
        private final BiFunction<String, Boolean, String> messageFn;
        private final Function<@NotNull TARGET, Boolean> condition;
        private final AssertionsChain<PREVIOUS, CURRENT> parent;

        public NullAssertions(
                Function<PREVIOUS, CURRENT> currentNestFn,
                Function<@NotNull CURRENT, @NotNull Optional<TARGET>> valueFn,
                Supplier<String> fieldNameSupplier,
                BiFunction<String, Boolean, String> messageFn,
                Function<@NotNull TARGET, Boolean> condition,
                AssertionsChain<PREVIOUS, CURRENT> parent) {
            this.currentNestFn = currentNestFn;
            this.valueFn = valueFn;
            this.fieldNameSupplier = fieldNameSupplier;
            this.messageFn = messageFn;
            this.condition = condition;
            this.parent = parent;
        }

        public AssertionsChain<PREVIOUS, CURRENT> allowNull() {
            return parent.withRule(t -> {
                var val = valueFn.apply(currentNestFn.apply(t));
                if (val.isPresent() && !condition.apply(val.get())) {
                    var fieldName = fieldNameSupplier.get();
                    return Optional.of(Violation.of(fieldName, messageFn.apply(fieldName, true)));
                }
                return Optional.empty();
            });
        }

        public AssertionsChain<PREVIOUS, CURRENT> rejectNull() {
            return parent.withRule(t -> {
                var val = valueFn.apply(currentNestFn.apply(t));
                if (val.isEmpty() || !condition.apply(val.get())) {
                    var fieldName = fieldNameSupplier.get();
                    return Optional.of(Violation.of(fieldName, messageFn.apply(fieldName, false)));
                }
                return Optional.empty();
            });
        }
    }

    final class Invalid<SUBJECT> implements StaticValid<SUBJECT> {

        private final List<Violation> violations;

        public Invalid(List<Violation> violations) {
            this.violations = violations;
        }

        @Override
        public <NEW_SUBJECT> StaticValid<NEW_SUBJECT> map(Function<SUBJECT, NEW_SUBJECT> mapper) {
            return self();
        }

        @Override
        public <NEW_SUBJECT> StaticValid<NEW_SUBJECT> flatMap(Function<SUBJECT, StaticValid<NEW_SUBJECT>> mapper) {
            return self();
        }

        @Override
        public SUBJECT orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
            throw StaticValid.hide(exceptionSupplier.get());
        }

        @Override
        public SUBJECT orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn) {
            throw StaticValid.hide(exceptionFn.apply(Collections.unmodifiableList(violations)));
        }

        @Override
        public Optional<SUBJECT> optional() {
            return Optional.empty();
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public boolean isNotValid() {
            return true;
        }

        @Override
        public List<Violation> violations() {
            return Collections.unmodifiableList(violations);
        }

        private <MIRROR> StaticValid<MIRROR> self() {
            @SuppressWarnings("unchecked")
            var self = (StaticValid<MIRROR>) this;
            return self;
        }
    }

    final class AlwaysValid<SUBJECT> implements StaticValid<SUBJECT> {

        private final Supplier<SUBJECT> value;

        public AlwaysValid(Supplier<SUBJECT> value) {
            this.value = value;
        }

        @Override
        public <R> StaticValid<R> map(Function<SUBJECT, R> mapper) {
            return new AlwaysValid<>(() -> mapper.apply(value.get()));
        }

        @Override
        public <R> StaticValid<R> flatMap(Function<SUBJECT, StaticValid<R>> mapper) {
            return mapper.apply(value.get());
        }

        @Override
        public SUBJECT orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
            return value.get();
        }

        @Override
        public SUBJECT orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn) {
            return value.get();
        }

        @Override
        public Optional<SUBJECT> optional() {
            return Optional.of(value.get());
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean isNotValid() {
            return false;
        }

        @Override
        public List<Violation> violations() {
            return List.of();
        }
    }

    interface Violation {

        String field();

        String message();

        static Violation of(String message) {
            return of("", message);
        }

        static Violation of(String field, String message) {
            Objects.requireNonNull(field);
            Objects.requireNonNull(message);
            return new Violation() {

                @Override
                public String field() {
                    return field;
                }

                @Override
                public String message() {
                    return message;
                }

                @Override
                public boolean equals(@Nullable Object obj) {
                    if (this == obj) {
                        return true;
                    }
                    if (obj == null || getClass() != obj.getClass()) {
                        return false;
                    }
                    var that = (Violation) obj;
                    return Objects.equals(field, that.field()) &&
                            Objects.equals(message, that.message());
                }

                @Override
                public int hashCode() {
                    return Objects.hash(field, message);
                }

                @Override
                public String toString() {
                    return "Violation[" +
                            "field='" + field + '\'' +
                            ", message='" + message + '\'' +
                            ']';
                }
            };
        }
    }

    private static <IN, OUT> Function<@NotNull IN, @NotNull Optional<OUT>> wrapOptional(Function<@NotNull IN, @Nullable OUT> fn) {
        return t -> Optional.ofNullable(fn.apply(t));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T hide(Throwable t) throws T {
        throw (T) t;
    }
}