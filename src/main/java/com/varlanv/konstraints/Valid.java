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
public interface Valid<T> {

    <R> Valid<R> map(Function<T, R> mapper);

    <R> Valid<R> flatMap(Function<T, Valid<R>> mapper);

    T orElseThrow(Supplier<? extends Throwable> exceptionSupplier);

    T orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn);

    Optional<T> optional();

    boolean isValid();

    boolean isNotValid();

    @Unmodifiable
    List<Violation> violations();

    static <T> Valid<T> ofValid(T value) {
        Objects.requireNonNull(value);
        return new AlwaysValid<>(() -> value);
    }

    static <T> Valid<T> ofValid(Supplier<@NotNull T> supplier) {
        return new AlwaysValid<>(Objects.requireNonNull(supplier));
    }

    static <T> Valid<T> ofInvalid(List<Violation> violations) {
        if (violations.isEmpty()) {
            throw new IllegalArgumentException("Violations must not be empty");
        }
        return new Invalid<>(List.copyOf(violations));
    }

    static <T> ValidationSpec<T> validationSpec(BiFunction<T, AssertionsChain<T, T>, AssertionsChain<T, ?>> specAction) {
        return ValidationSpec.fromFn(specAction);
    }

    interface ValidationSpec<T> {

        Function<T, Valid<T>> toValidationFunction();

        UnaryOperator<T> toValidationFunction(Function<List<Violation>, ? extends Throwable> onException);

        UnaryOperator<T> toValidationFunction(Supplier<? extends Throwable> onException);

        Valid<T> validate(T t);

        static <T> ValidationSpec<T> fromFn(BiFunction<T, AssertionsChain<T, T>, AssertionsChain<T, ?>> specAction) {
            Objects.requireNonNull(specAction);
            return new ValidationSpec<>() {
                @Override
                public Function<T, Valid<T>> toValidationFunction() {
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
                public Valid<T> validate(T t) {
                    Objects.requireNonNull(t);
                    var violations = new ArrayList<Violation>(1);
                    var chain = new AssertionsChain<T, T>(t, violations);
                    specAction.apply(t, chain);
                    if (violations.isEmpty()) {
                        return Valid.ofValid(t);
                    } else {
                        return new Invalid<>(violations);
                    }
                }
            };
        }

        static <T> ValidationSpec<T> fromRules(List<Violation> violations) {
            Objects.requireNonNull(violations);
            return new ValidationSpec<>() {

                @Override
                public Function<T, Valid<T>> toValidationFunction() {
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
                public Valid<T> validate(T t) {
                    Objects.requireNonNull(t);
                    if (violations.isEmpty()) {
                        return Valid.ofValid(t);
                    } else {
                        return new Invalid<>(violations);
                    }
                }
            };
        }
    }

    final class IterableSpec<A, R, I extends Iterable<R>, T, N> {

        private final Supplier<Optional<I>> valueFn;
        private final String fieldName;
        private final NullSpec<T, N> nullSpec;
        private final Function<Supplier<Optional<R>>, A> assertionsSupplier;

        public IterableSpec(Supplier<Optional<I>> valueFn,
                            String fieldName,
                            NullSpec<T, N> nullSpec,
                            Function<Supplier<Optional<R>>, A> assertionsSupplier) {
            this.valueFn = valueFn;
            this.fieldName = fieldName;
            this.nullSpec = nullSpec;
            this.assertionsSupplier = assertionsSupplier;
        }

        public AssertionsChain<T, N> eachItem(Function<A, AssertionsChain<T, N>> specAction) {
            return nullSpec.parent();
        }
    }

    final class IterablePickSpec<T, N> {

        private final NullSpec<T, N> nullSpec;

        public IterablePickSpec(NullSpec<T, N> nullSpec) {
            this.nullSpec = nullSpec;
        }

        public <I extends Iterable<String>> AssertionsChain<T, N> strings(Function<@NotNull N, @Nullable I> valueFn,
                                                                          String fieldName,
                                                                          Function<IterableSpec<StringAssertions<T, N>, String, I, T, N>, AssertionsChain<T, N>> s) {
            ;
            return s.apply(new IterableSpec<>(
                    wrapOptionalSupplier(valueFn).apply(nullSpec.currentValue),
                    fieldName,
                    nullSpec,
                    val -> new StringAssertions<>(val, () -> "", nullSpec)));
        }

        public <R extends Comparable<R>, I extends Iterable<R>> AssertionsChain<T, N> numbers(Function<@NotNull N, @Nullable I> valueFn,
                                                                                              String fieldName,
                                                                                              Function<IterableSpec<NumberAssertions<R, T, N>, R, I, T, N>, AssertionsChain<T, N>> s) {
            return s.apply(new IterableSpec<>(
                    wrapOptionalSupplier(valueFn).apply(nullSpec.currentValue),
                    fieldName,
                    nullSpec,
                    val -> new NumberAssertions<>(val, () -> "", nullSpec)));
        }

        public <R,I extends Iterable<R>> AssertionsChain<T, N> nested(Function<@NotNull N, @Nullable I> valueFn,
                                                                         String fieldName,
                                                                         Function<IterableSpec<StringAssertions<T, N>, String, I, T, N>, AssertionsChain<T, N>> s) {
            return s.apply(new IterableSpec<>(
                    wrapOptionalSupplier(valueFn).apply(nullSpec.currentValue),
                    fieldName,
                    nullSpec,
                    val -> new StringAssertions<>(val, () -> "", nullSpec)));
        }
    }

    final class NullSpec<T, N> {

        private final AssertionsChain<T, N> parent;
        private final String currentFieldName;
        private final N currentValue;
        private final Boolean allowNull;

        public NullSpec(AssertionsChain<T, N> parent,
                        String currentFieldName,
                        N currentValue,
                        Boolean allowNull) {
            this.parent = parent;
            this.currentFieldName = currentFieldName;
            this.currentValue = currentValue;
            this.allowNull = allowNull;
        }

        public StringAssertions<T, N> stringField(Function<@NotNull N, @Nullable String> stringValueFn,
                                                  String fieldName) {
            return new StringAssertions<>(
                    wrapOptionalSupplier(stringValueFn).apply(currentValue),
                    () -> fieldNameFormat(currentFieldName, fieldName),
                    this
            );
        }

        public <R extends Comparable<R>> NumberAssertions<R, T, N> numberField(Function<@NotNull N, @Nullable R> valueFn,
                                                                               String fieldName) {
            return new NumberAssertions<>(
                    wrapOptionalSupplier(valueFn).apply(currentValue),
                    () -> fieldNameFormat(currentFieldName, fieldName),
                    this
            );
        }

        public IterablePickSpec<T, N> iterableField() {
            return new IterablePickSpec<>(this);
        }


        public <R> AssertionsChain<T, N> nestedField(Function<@NotNull N, @Nullable R> valueFn,
                                                     String fieldName,
                                                     BiFunction<R, AssertionsChain<T, R>, AssertionsChain<T, R>> specAction) {
            var newValue = valueFn.apply(currentValue);
            if (newValue == null) {
                return parent.withViolation(Violation.of(currentFieldName, "Field [%s] is expected to be non-null".formatted(currentFieldName)));
            }
            var newFieldName = currentFieldName.isEmpty() ? fieldName : currentFieldName + "." + fieldName;
            specAction.apply(newValue, new AssertionsChain<>(newFieldName, newValue, parent.violations()));
            return parent;
        }

        Boolean allowNull() {
            return allowNull;
        }

        AssertionsChain<T, N> parent() {
            return parent;
        }
    }

    final class AssertionsChain<T, N> {

        private final String currentFieldName;
        private final N currentValue;
        private final List<Violation> violations;

        public AssertionsChain(N currentValue, List<Violation> violations) {
            this.currentFieldName = "";
            this.currentValue = currentValue;
            this.violations = violations;
        }

        public AssertionsChain(String currentFieldName, N currentValue, List<Violation> violations) {
            if (currentFieldName.isBlank()) {
                throw new IllegalArgumentException("Field name must not be blank");
            }
            this.currentFieldName = currentFieldName;
            this.currentValue = currentValue;
            this.violations = violations;
        }

        public AssertionsChain<T, N> assertNull(Function<@NotNull N, @Nullable Object> valueFn, String fieldName) {
            if (valueFn.apply(currentValue) != null) {
                return withViolation(Violation.of(fieldName, "Field [%s] is expected to be null".formatted(fieldNameFormat(currentFieldName, fieldName))));
            } else {
                return this;
            }
        }

        public AssertionsChain<T, N> assertNotNull(Function<@NotNull N, @Nullable Object> valueFn, String fieldName) {
            if (valueFn.apply(currentValue) == null) {
                return withViolation(Violation.of(fieldName, "Field [%s] is expected to be non-null".formatted(fieldNameFormat(currentFieldName, fieldName))));
            } else {
                return this;
            }
        }

        public NullSpec<T, N> nullable() {
            return new NullSpec<>(this, currentFieldName, currentValue, true);
        }

        public NullSpec<T, N> nonNull() {
            return new NullSpec<>(this, currentFieldName, currentValue, false);
        }

        AssertionsChain<T, N> withViolation(Violation violation) {
            violations.add(violation);
            return this;
        }

        List<Violation> violations() {
            return violations;
        }
    }

    final class NumberAssertions<R extends Comparable<R>, T, N> {

        private final Supplier<Optional<R>> valueSupplier;
        private final Supplier<String> fieldName;
        private final NullSpec<T, N> nullSpec;
        private final Comparator<R> comparator;

        public NumberAssertions(Supplier<Optional<R>> valueSupplier,
                                Supplier<String> fieldName,
                                NullSpec<T, N> nullSpec) {
            this.valueSupplier = valueSupplier;
            this.fieldName = fieldName;
            this.nullSpec = nullSpec;
            this.comparator = Comparable::compareTo;
        }

        public AssertionsChain<T, N> isGte(R target) {
            Objects.requireNonNull(target);
            return check(
                    valueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or number greater than or equal to [%s]".formatted(target),
                    "must be not null and greater than or equal to [%s]".formatted(target),
                    val -> comparator.compare(val, target) >= 0
            );
//            );
        }

        public AssertionsChain<T, N> isLte(R target) {
            Objects.requireNonNull(target);
            return check(
                    valueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or number less than [%s]".formatted(target),
                    "must be not null and less than [%s]".formatted(target),
                    val -> comparator.compare(val, target) <= 0
            );
        }

        public AssertionsChain<T, N> isInRange(R minTarget, R maxTarget) {
            if (comparator.compare(minTarget, maxTarget) > 0) {
                throw new IllegalArgumentException("minTarget must be less than maxTarget");
            }
            return check(
                    valueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or integer in range [%s - %s]".formatted(minTarget, maxTarget),
                    "must be not null and in range [%s - %s]".formatted(minTarget, maxTarget),
                    val -> comparator.compare(val, minTarget) >= 0 && comparator.compare(val, maxTarget) <= 0
            );
        }
    }

    private static <R, T, N> AssertionsChain<T, N> check(Supplier<@NotNull Optional<R>> valSupplier,
                                                         NullSpec<T, N> nullSpec,
                                                         Supplier<String> fieldNameSupplier,
                                                         String nullableFailMessage,
                                                         String nonNullFailMessage,
                                                         Function<@NotNull R, @NotNull Boolean> condition) {
        var maybeVal = valSupplier.get();
        if (maybeVal.isEmpty()) {
            if (!nullSpec.allowNull()) {
                return nullSpec.parent().withViolation(Violation.of(fieldNameSupplier.get(), nullableFailMessage));
            }
        } else {
            var val = maybeVal.get();
            if (!condition.apply(val)) {
                return nullSpec.parent().withViolation(Violation.of(fieldNameSupplier.get(), nonNullFailMessage));
            }
        }
        return nullSpec.parent();
    }

    final class StringAssertions<T, N> {

        private final Supplier<Optional<String>> stringValueSupplier;
        private final Supplier<String> fieldName;
        private final NullSpec<T, N> nullSpec;

        public StringAssertions(Supplier<Optional<String>> stringValueSupplier,
                                Supplier<String> fieldName,
                                NullSpec<T, N> nullSpec) {
            this.stringValueSupplier = stringValueSupplier;
            this.fieldName = fieldName;
            this.nullSpec = nullSpec;
        }


        public AssertionsChain<T, N> assertEmpty() {
            return check(
                    stringValueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or empty string",
                    "must be not null and empty string",
                    String::isEmpty
            );
        }

        public AssertionsChain<T, N> assertNotBlank() {
            return check(
                    stringValueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or non-blank string",
                    "must be not null and non-blank string",
                    val -> !val.isBlank()
            );
        }

        public AssertionsChain<T, N> assertLength(Integer length) {
            Objects.requireNonNull(length);
            return check(
                    stringValueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or string with length [%d]".formatted(length),
                    "must be not null and have length [%d]".formatted(length),
                    val -> val.length() == length
            );
        }

        public AssertionsChain<T, N> assertMinLength(Integer minLength) {
            Objects.requireNonNull(minLength);
            return check(
                    stringValueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or string with min length [%d]".formatted(minLength),
                    "must be not null and have min length [%d]".formatted(minLength),
                    val -> val.length() >= minLength
            );
        }

        public AssertionsChain<T, N> assertMaxLength(Integer maxLength) {
            Objects.requireNonNull(maxLength);
            return check(
                    stringValueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or string with max length [%d]".formatted(maxLength),
                    "must be not null and have max length [%d]".formatted(maxLength),
                    val -> val.length() <= maxLength
            );
        }

        public AssertionsChain<T, N> assertLengthRange(Integer minLength, Integer maxLength) {
            Objects.requireNonNull(minLength);
            Objects.requireNonNull(maxLength);
            return check(
                    stringValueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or string with length range [%d - %d]".formatted(minLength, maxLength),
                    "must be not null and have length range [%d - %d]".formatted(minLength, maxLength),
                    val -> val.length() >= minLength && val.length() <= maxLength
            );
        }

        public AssertionsChain<T, N> assertMatches(Pattern pattern) {
            Objects.requireNonNull(pattern);
            return check(
                    stringValueSupplier,
                    nullSpec,
                    fieldName,
                    "must be null or string that matches pattern [%s]".formatted(pattern.pattern()),
                    "must be not null and match pattern [%s]".formatted(pattern.pattern()),
                    val -> pattern.matcher(val).matches()
            );
        }
    }

    final class NullAssertions<R, T, N> {

        private final N currentValue;
        private final Function<@NotNull N, @NotNull Optional<R>> valueFn;
        private final Supplier<String> fieldNameSupplier;
        private final BiFunction<String, Boolean, String> messageFn;
        private final Function<@NotNull R, Boolean> condition;
        private final AssertionsChain<T, N> parent;

        public NullAssertions(N currentValue,
                              Function<@NotNull N, @NotNull Optional<R>> valueFn,
                              Supplier<String> fieldNameSupplier,
                              BiFunction<String, Boolean, String> messageFn,
                              Function<@NotNull R, Boolean> condition,
                              AssertionsChain<T, N> parent) {
            this.currentValue = currentValue;
            this.valueFn = valueFn;
            this.fieldNameSupplier = fieldNameSupplier;
            this.messageFn = messageFn;
            this.condition = condition;
            this.parent = parent;
        }

        public AssertionsChain<T, N> allowingNull() {
            var val = valueFn.apply(currentValue);
            if (val.isPresent() && !condition.apply(val.get())) {
                var fieldName = fieldNameSupplier.get();
                return parent.withViolation(Violation.of(fieldName, messageFn.apply(fieldName, true)));
            }
            return parent;
        }

        public AssertionsChain<T, N> rejectingNull() {
            var val = valueFn.apply(currentValue);
            if (val.isEmpty() || !condition.apply(val.get())) {
                var fieldName = fieldNameSupplier.get();
                return parent.withViolation(Violation.of(fieldName, messageFn.apply(fieldName, false)));
            }
            return parent;
        }
    }

    final class Invalid<T> implements Valid<T> {

        private final List<Violation> violations;

        public Invalid(List<Violation> violations) {
            this.violations = violations;
        }

        @Override
        public <R> Valid<R> map(Function<T, R> mapper) {
            return self();
        }

        @Override
        public <R> Valid<R> flatMap(Function<T, Valid<R>> mapper) {
            return self();
        }

        @Override
        public T orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
            throw Valid.hide(exceptionSupplier.get());
        }

        @Override
        public T orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn) {
            throw Valid.hide(exceptionFn.apply(Collections.unmodifiableList(violations)));
        }

        @Override
        public Optional<T> optional() {
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

        private <R> Valid<R> self() {
            @SuppressWarnings("unchecked")
            var self = (Valid<R>) this;
            return self;
        }
    }

    final class AlwaysValid<T> implements Valid<T> {

        private final Supplier<T> value;

        public AlwaysValid(Supplier<T> value) {
            this.value = value;
        }

        @Override
        public <R> Valid<R> map(Function<T, R> mapper) {
            return new AlwaysValid<>(() -> mapper.apply(value.get()));
        }

        @Override
        public <R> Valid<R> flatMap(Function<T, Valid<R>> mapper) {
            return mapper.apply(value.get());
        }

        @Override
        public T orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
            return value.get();
        }

        @Override
        public T orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn) {
            return value.get();
        }

        @Override
        public Optional<T> optional() {
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

    private static <T, R> Function<@NotNull T, @NotNull Optional<R>> wrapOptional(Function<@NotNull T, @Nullable R> fn) {
        return t -> Optional.ofNullable(fn.apply(t));
    }

    private static <T, R> Function<@NotNull T, @NotNull Supplier<@NotNull Optional<R>>> wrapOptionalSupplier(Function<@NotNull T, @Nullable R> fn) {
        return t -> () -> Optional.ofNullable(fn.apply(t));
    }

    private static String fieldNameFormat(String currentObjectField, String field) {
        return currentObjectField.isEmpty() ? field : currentObjectField + "." + field;
    }

    private static Supplier<String> fieldNameFormatSupplier(Supplier<String> currentObjectField, String field) {
        return () -> fieldNameFormat(currentObjectField.get(), field);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T hide(Throwable t) throws T {
        throw (T) t;
    }
}