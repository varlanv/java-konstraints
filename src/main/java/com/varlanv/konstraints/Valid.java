package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * The Valid class represents a container for validation results, encapsulating either a valid value
 * or a list of violations. It provides methods to transform, check, and retrieve the validation state
 * and its contents, facilitating functional programming-style validation pipelines.
 *
 * @param <T> the type of the value being validated
 */
@NotNullByDefault
public interface Valid<T> {

    /**
     * Transforms the current valid value using the provided mapping function.
     * If the current object represents an invalid state, the mapping function is not applied
     * and the invalid state is retained.
     *
     * @param <R>    the type of the resulting mapped value
     * @param mapper a function to apply to the valid value
     * @return a new Valid object containing the result of applying the mapper to the current value
     * if the current object is in a valid state; otherwise, retains the invalid state
     */
    <R> Valid<R> map(Function<T, R> mapper);

    /**
     * Transforms the current valid value using a given mapping function that itself returns a new Valid object.
     * If the current object represents an invalid state, the mapping function is not applied,
     * and the invalid state is retained.
     *
     * @param <R>    the type of the resulting Valid object's value
     * @param mapper a function that takes the current valid value as input and returns a new Valid object
     * @return a new Valid object containing the result of applying the mapper to the current value,
     * or the current invalid state if this object is invalid
     */
    <R> Valid<R> flatMap(Function<T, Valid<R>> mapper);

    /**
     * Returns the valid value if the current object represents a valid state.
     * Otherwise, throws the exception provided by the given {@code exceptionSupplier}.
     *
     * @param exceptionSupplier a supplier providing the exception to be thrown if the current object is invalid
     * @return the valid value if the current object is in a valid state
     */
    T orElseThrow(Supplier<? extends Throwable> exceptionSupplier);

    /**
     * Returns the valid value if the current object represents a valid state.
     * Otherwise, throws the exception provided by the given {@code exceptionFn}.
     *
     * @param exceptionFn a function that takes a list of violations and returns an exception to be thrown
     *                    if the current object is invalid
     * @return the valid value if the current object is in a valid state
     */
    T orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn);

    /**
     * Retrieves the contained value as an Optional.
     * If the current object represents a valid state, the Optional will contain the value.
     * If the current object represents an invalid state, an empty Optional is returned.
     *
     * @return an Optional containing the value if the object is valid, or an empty Optional otherwise
     */
    Optional<T> optional();

    /**
     * Indicates whether the current object represents a valid state.
     *
     * @return true if the object is in a valid state, false otherwise
     */
    boolean isValid();

    /**
     * Determines whether the current object represents an invalid state.
     *
     * @return true if the object is in an invalid state, false otherwise
     */
    boolean isNotValid();

    /**
     * Retrieves the list of violations if the current object represents an invalid state.
     * If the object is in a valid state, the list will be empty.
     *
     * @return an unmodifiable list of violations indicating why the object is invalid,
     * or an empty list if the object is valid
     */
    @Unmodifiable
    List<Violation> violations();

    /**
     * Creates a valid {@code Valid} instance containing the specified value.
     *
     * @param value the value to be wrapped in a valid {@code Valid} instance; must not be {@code null}
     * @return a {@code Valid} instance that represents a valid state containing the provided value
     * @throws NullPointerException if the {@code value} is {@code null}
     */
    static <T> Valid<T> ofValid(T value) {
        Objects.requireNonNull(value);
        return new AlwaysValid<>(() -> value);
    }

    static <T> Valid<T> ofValid(Supplier<@NotNull T> supplier) {
        return new AlwaysValid<>(Objects.requireNonNull(supplier));
    }

    /**
     * Creates an {@code Invalid} instance containing the specified list of violations.
     * The list of violations must not be empty.
     *
     * @param <T>        the type of the valid value (not applicable for invalid state)
     * @param violations a list of {@code Violation} objects describing the reasons
     *                   for the invalid state; must not be empty
     * @return a {@code Valid} instance representing an invalid state containing the
     * provided list of violations
     * @throws IllegalArgumentException if the {@code violations} list is empty
     */
    static <T> Valid<T> ofInvalid(List<Violation> violations) {
        if (violations.isEmpty()) {
            throw new IllegalArgumentException("Violations must not be empty");
        }
        return new Invalid<>(List.copyOf(violations));
    }

    static <T> ValidationSpec<T> validationSpecV2(BiFunction<T, AssertionsChain<T, T>, AssertionsChain<T, ?>> specAction) {
        return ValidationSpec.fromFn(specAction);
    }

    interface Rules<T> {

        Rules<T> add(Rule<T> rule);

        @Unmodifiable
        List<Violation> apply(T t);

        static <T> Rules<T> create() {
            var rules = new ArrayList<Rule<T>>();
            return new Rules<>() {

                @Override
                public Rules<T> add(Rule<T> rule) {
                    rules.add(Objects.requireNonNull(rule));
                    return this;
                }

                @Override
                public List<Violation> apply(T t) {
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

    /**
     * A specification for defining validation rules and applying them to objects of type T.
     * Provides methods for converting validation logic into reusable functions, as well as
     * directly validating objects and handling validation errors.
     *
     * @param <T> the type of the object to be validated
     */
    interface ValidationSpec<T> {

        /**
         * Converts the validation specification into a reusable function that can validate
         * objects of type T and produce a {@link Valid} result.
         *
         * @return a function that takes an object of type T, applies the validation rules
         * defined in the specification, and returns a {@link Valid} instance that
         * encapsulates the validation result.
         */
        Function<T, Valid<T>> toValidationFunction();

        /**
         * Converts the validation specification into a reusable unary operator
         * that validates objects of type T, applying the specified exception-handling function
         * for any validation errors encountered.
         *
         * @param onException a function that converts a list of {@link Violation} objects
         *                    (representing validation errors) into an exception of a specified type
         * @return a unary operator that validates an object of type T and throws the exception
         * provided by the {@code onException} function if validation errors occur
         */
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
                    var rules = Rules.<T>create();
                    var chain = new AssertionsChain<>(t, rules);
                    specAction.apply(t, chain);

                    var violations = rules.apply(t);
                    if (violations.isEmpty()) {
                        return Valid.ofValid(t);
                    } else {
                        return new Invalid<>(violations);
                    }
                }
            };
        }

        static <T> ValidationSpec<T> fromRules(Rules<T> rules) {
            Objects.requireNonNull(rules);
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
                    var violations = rules.apply(t);
                    if (violations.isEmpty()) {
                        return Valid.ofValid(t);
                    } else {
                        return new Invalid<>(violations);
                    }
                }
            };
        }
    }

    interface Rule<T> extends Function<@NotNull T, @NotNull Optional<Violation>> {
    }

    final class AssertionsChain<T, N> {

        private final String currentFieldName;
        private final N currentValue;
        private final Rules<T> rules;

        public AssertionsChain(N currentValue, Rules<T> rules) {
            this.currentFieldName = "";
            this.currentValue = currentValue;
            this.rules = rules;
        }

        public AssertionsChain(String currentFieldName, N currentValue, Rules<T> rules) {
            if (currentFieldName.isBlank()) {
                throw new IllegalArgumentException("Field name must not be blank");
            }
            this.currentFieldName = currentFieldName;
            this.currentValue = currentValue;
            this.rules = rules;
        }

        public <R> AssertionsChain<T, N> extractingNonNull(Function<@NotNull N, @Nullable R> valueFn,
                                                           String fieldName,
                                                           BiFunction<R, AssertionsChain<T, R>, AssertionsChain<T, R>> specAction) {
            var newValue = valueFn.apply(currentValue);
            if (newValue == null) {
                rules.add(t -> Optional.of(Violation.of(currentFieldName, "Field [%s] is expected to be non-null".formatted(currentFieldName))));
                return this;
            }
            var newFieldName = currentFieldName.isEmpty() ? fieldName : currentFieldName + "." + fieldName;
            var apply = specAction.apply(newValue, new AssertionsChain<>(newFieldName, newValue, rules));
            return this;
        }

        public <R> AssertionsChain<T, N> extractingNullable(Function<@NotNull N, @Nullable R> valueFn,
                                                            String fieldName,
                                                            BiFunction<R, AssertionsChain<T, R>, AssertionsChain<T, R>> specAction) {
            var newValue = valueFn.apply(currentValue);
            if (newValue == null) {
                return this;
            }
            var newFieldName = currentFieldName.isEmpty() ? fieldName : currentFieldName + "." + fieldName;
            specAction.apply(newValue, new AssertionsChain<>(newFieldName, newValue, rules));
            return this;
        }
//
//        public <R> AssertionsChain<T, ?> extractingIterableNonNull(Function<@NotNull N, @Nullable Iterable<R>> valueFn,
//                                                                   String fieldName,
//                                                                   BiFunction<Iterable<R>, AssertionsChain<T, Iterable<R>>, AssertionsChain<T, ?>> specAction) {
//            var newValue = valueFn.apply(currentValue);
//            if (newValue == null) {
//                rules.add(t -> Optional.of(Violation.of(currentFieldName, "Field [%s] is expected to be non-null".formatted(currentFieldName))));
//                return this;
//            }
//            var newFieldName = currentFieldName.isEmpty() ? fieldName : currentFieldName + "." + fieldName;
//            return specAction.apply(newValue, new AssertionsChain<>(newFieldName, newValue, rules));
//        }
//
//        public <R> AssertionsChain<T, ?> extractingIterableNullable(Function<@NotNull N, @Nullable Iterable<R>> valueFn,
//                                                                    String fieldName,
//                                                                    BiFunction<R, AssertionsChain<T, R>, AssertionsChain<T, ?>> specAction) {
//            var newValue = valueFn.apply(currentValue);
//            if (newValue == null) {
//                return this;
//            }
//            var newFieldName = currentFieldName.isEmpty() ? fieldName : currentFieldName + "." + fieldName;
//            return specAction.apply(newValue, new AssertionsChain<>(newFieldName, newValue, rules));
//        }

        public AssertionsChain<T, N> isNull(Function<@NotNull N, @Nullable Object> valueFn, String fieldName) {
            rules.add(t -> {
                if (valueFn.apply(currentValue) != null) {
                    return Optional.of(Violation.of(fieldName, "Field [%s] is expected to be null".formatted(fieldNameFormat(currentFieldName, fieldName))));
                } else {
                    return Optional.empty();
                }
            });
            return this;
        }

        public AssertionsChain<T, N> isNotNull(Function<@NotNull N, @Nullable Object> valueFn, String fieldName) {
            rules.add(t -> {
                if (valueFn.apply(currentValue) == null) {
                    return Optional.of(Violation.of(fieldName, "Field [%s] is expected to be non-null".formatted(fieldNameFormat(currentFieldName, fieldName))));
                } else {
                    return Optional.empty();
                }
            });
            return this;
        }

        public StringAssertions<T, N> stringField(Function<@NotNull N, @Nullable String> stringValueFn,
                                                  String fieldName) {
            Function<N, Optional<String>> fn = wrapOptional(stringValueFn);
            return new StringAssertions<>(currentValue, fn, () -> fieldNameFormat(currentFieldName, fieldName), this);
        }

        public NumberAssertions<Integer, T, N> integerField(Function<@NotNull N, @Nullable Integer> integerValueFn,
                                                            String fieldName) {
            Function<N, Optional<Integer>> fn = wrapOptional(integerValueFn);
            return new NumberAssertions<>(currentValue, fn, Integer::compareTo, () -> fieldNameFormat(currentFieldName, fieldName), this);
        }

        public NumberAssertions<Long, T, N> longField(Function<@NotNull N, @Nullable Long> longValueFn, String fieldName) {
            Function<N, Optional<Long>> fn = wrapOptional(longValueFn);
            return new NumberAssertions<>(currentValue, fn, Long::compareTo, () -> fieldNameFormat(currentFieldName, fieldName), this);
        }

        public NumberAssertions<Double, T, N> doubleField(Function<@NotNull N, @Nullable Double> doubleValueFn, String fieldName) {
            Function<N, Optional<Double>> fn = wrapOptional(doubleValueFn);
            return new NumberAssertions<>(currentValue, fn, Double::compareTo, () -> fieldNameFormat(currentFieldName, fieldName), this);
        }

        public NumberAssertions<BigDecimal, T, N> decimalField(Function<@NotNull N, @Nullable BigDecimal> decimalValueFn, String fieldName) {
            Function<N, Optional<BigDecimal>> fn = wrapOptional(decimalValueFn);
            return new NumberAssertions<>(currentValue, fn, BigDecimal::compareTo, () -> fieldNameFormat(currentFieldName, fieldName), this);
        }

        AssertionsChain<T, N> withRule(Rule<T> rule) {
            rules.add(rule);
            return this;
        }

        Rules<T> rules() {
            return rules;
        }
    }

    final class NumberAssertions<R extends Number, T, N> {

        private final N currentValue;
        private final Function<@NotNull N, @NotNull Optional<R>> numberValueFn;
        private final Comparator<R> comparator;
        private final Supplier<String> fieldName;
        private final AssertionsChain<T, N> parent;

        public NumberAssertions(N currentValue,
                                Function<@NotNull N, @NotNull Optional<R>> numberValueFn,
                                Comparator<R> comparator,
                                Supplier<String> fieldName,
                                AssertionsChain<T, N> parent) {
            this.currentValue = currentValue;
            this.numberValueFn = numberValueFn;
            this.comparator = comparator;
            this.fieldName = fieldName;
            this.parent = parent;
        }

        public NullAssertions<R, T, N> isGte(R target) {
            Objects.requireNonNull(target);
            return new NullAssertions<>(
                    currentValue,
                    numberValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or number greater than or equal to [%d]" :
                            "Field [%s] must be not null and greater than or equal to [%d]").formatted(fieldName, target),
                    val -> comparator.compare(val, target) >= 0,
                    parent
            );
        }

        public NullAssertions<R, T, N> isLte(R target) {
            Objects.requireNonNull(target);
            return new NullAssertions<>(
                    currentValue,
                    numberValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or number less than [%d]" :
                            "Field [%s] must be not null and less than [%d]").formatted(fieldName, target),
                    val -> comparator.compare(val, target) <= 0,
                    parent
            );
        }

        public NullAssertions<R, T, N> isInRange(R minTarget, R maxTarget) {
            if (comparator.compare(minTarget, maxTarget) > 0) {
                throw new IllegalArgumentException("minTarget must be less than maxTarget");
            }
            return new NullAssertions<>(
                    currentValue,
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

    final class StringAssertions<T, N> {

        private final N currentValue;
        private final Function<@NotNull N, @NotNull Optional<String>> stringValueFn;
        private final Supplier<String> fieldName;
        private final AssertionsChain<T, N> parent;

        public StringAssertions(N currentValue,
                                Function<@NotNull N, @NotNull Optional<String>> stringValueFn,
                                Supplier<String> fieldName,
                                AssertionsChain<T, N> parent) {
            this.currentValue = currentValue;
            this.stringValueFn = stringValueFn;
            this.fieldName = fieldName;
            this.parent = parent;
        }

        public NullAssertions<String, T, N> isEmpty() {
            return new NullAssertions<>(
                    currentValue,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or empty string" :
                            "Field [%s] must be not null and empty string").formatted(fieldName),
                    String::isEmpty,
                    parent
            );
        }

        public NullAssertions<String, T, N> isNotBlank() {
            return new NullAssertions<>(
                    currentValue,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or non-blank string" :
                            "Field [%s] must be not null and non-blank string").formatted(fieldName),
                    val -> !val.isBlank(),
                    parent
            );
        }

        public NullAssertions<String, T, N> hasLength(Integer length) {
            Objects.requireNonNull(length);
            return new NullAssertions<>(
                    currentValue,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or string with length [%d]" :
                            "Field [%s] must be not null and have length [%d]").formatted(fieldName, length),
                    val -> val.length() == length,
                    parent
            );
        }

        public NullAssertions<String, T, N> hasMinLength(Integer minLength) {
            Objects.requireNonNull(minLength);
            return new NullAssertions<>(
                    currentValue,
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

        public NullAssertions<String, T, N> hasMaxLength(Integer maxLength) {
            Objects.requireNonNull(maxLength);
            return new NullAssertions<>(
                    currentValue,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or string with max length [%d]" :
                            "Field [%s] must be not null and have max length [%d]").formatted(fieldName, maxLength),
                    val -> val.length() <= maxLength,
                    parent
            );
        }

        public NullAssertions<String, T, N> hasLengthRange(Integer minLength, Integer maxLength) {
            Objects.requireNonNull(minLength);
            Objects.requireNonNull(maxLength);
            return new NullAssertions<>(
                    currentValue,
                    stringValueFn,
                    fieldName,
                    (fieldName, allowNull) -> (allowNull ?
                            "Field [%s] must be null or string with length range [%d - %d]" :
                            "Field [%s] must be not null and have length range [%d - %d]").formatted(fieldName, minLength, maxLength),
                    val -> val.length() >= minLength && val.length() <= maxLength,
                    parent
            );
        }

        /**
         * Validates whether the field matches the specified regular expression pattern.
         * The validation ensures that the string value of the field adheres to the given pattern.
         * This method can be chained with further assertions or validations.
         *
         * @param pattern the regular expression {@code Pattern} to match the field value against
         * @return an instance of {@code NullAssertions<String, T>} to specify additional
         * conditions or validations for the field
         * @throws NullPointerException if the specified {@code pattern} is null
         */
        public NullAssertions<String, T, N> matches(Pattern pattern) {
            Objects.requireNonNull(pattern);
            return new NullAssertions<>(
                    currentValue,
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

        /**
         * Allows null values for the field being validated. If the value is null,
         * no violation will be recorded. If the value is non-null, the provided
         * condition is applied to determine any violations.
         *
         * @return the parent {@code AssertionsChain} instance to continue defining validation rules
         */
        public AssertionsChain<T, N> allowingNull() {
            return parent.withRule(t -> {
                var val = valueFn.apply(currentValue);
                if (val.isPresent() && !condition.apply(val.get())) {
                    var fieldName = fieldNameSupplier.get();
                    return Optional.of(Violation.of(fieldName, messageFn.apply(fieldName, true)));
                }
                return Optional.empty();
            });
        }

        /**
         * Enforces a non-null constraint on the field being validated.
         * If the value is null or does not satisfy the provided condition,
         * a violation is recorded with a corresponding message.
         *
         * @return the parent {@code AssertionsChain} instance to continue defining validation rules
         */
        public AssertionsChain<T, N> rejectingNull() {
            return parent.withRule(t -> {
                var val = valueFn.apply(currentValue);
                if (val.isEmpty() || !condition.apply(val.get())) {
                    var fieldName = fieldNameSupplier.get();
                    return Optional.of(Violation.of(fieldName, messageFn.apply(fieldName, false)));
                }
                return Optional.empty();
            });
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

    private static String fieldNameFormat(String currentObjectField, String field) {
        return currentObjectField.isEmpty() ? field : currentObjectField + "." + field;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T hide(Throwable t) throws T {
        throw (T) t;
    }
}