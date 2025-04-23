package com.varlanv.konstraints;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface ValidationSpec<SUBJECT> {

  Function<SUBJECT, Valid<SUBJECT>> toValidationFunction();

  UnaryOperator<SUBJECT> toValidationFunction(Function<List<Violation>, ? extends Throwable> onException);

  UnaryOperator<SUBJECT> toValidationFunction(Supplier<? extends Throwable> onException);

  Valid<SUBJECT> validate(SUBJECT t);

  static <T> ValidationSpec<T> fromRules(Rules<T> rules) {
    Objects.requireNonNull(rules);
    return new ValidationSpec<>() {

      @Override
      public Function<T, Valid<T>> toValidationFunction() {
        return this::validate;
      }

      @Override
      public UnaryOperator<T> toValidationFunction(
          Function<List<Violation>, ? extends Throwable> onException) {
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
          return Valid.valid(t);
        } else {
          return Valid.invalid(violations);
        }
      }
    };
  }
}
