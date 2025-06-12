package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Valid<SUBJECT> {

  <NEXT_SUBJECT> Valid<NEXT_SUBJECT> map(Function<SUBJECT, NEXT_SUBJECT> mapper);

  <NEXT_SUBJECT> Valid<NEXT_SUBJECT> flatMap(Function<SUBJECT, Valid<NEXT_SUBJECT>> mapper);

  SUBJECT orElseThrow(Supplier<? extends Throwable> exceptionSupplier);

  SUBJECT orElseThrow(Function<Violations, ? extends Throwable> exceptionFn);

  Optional<SUBJECT> optional();

  boolean isValid();

  boolean isNotValid();

  Violations violations();

  static <SUBJECT> Valid<SUBJECT> valid(SUBJECT value) {
    Objects.requireNonNull(value);
    return valid(() -> value);
  }

  static <SUBJECT> Valid<SUBJECT> valid(Supplier<@NotNull SUBJECT> delegate) {
    var supplier = Internals.onceSupplier(delegate);
    return new Valid<>() {

      @Override
      public <R> Valid<R> map(Function<@NotNull SUBJECT, @NotNull R> mapper) {
        return valid(Internals.onceSupplier(() ->
                mapper.apply(
                    supplier.get()
                )
            )
        );
      }

      @Override
      public <R> Valid<R> flatMap(Function<@NotNull SUBJECT, @NotNull Valid<R>> mapper) {
        return mapper.apply(
            supplier.get()
        );
      }

      @Override
      public SUBJECT orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
        return supplier.get();
      }

      @Override
      public SUBJECT orElseThrow(Function<Violations, ? extends Throwable> exceptionFn) {
        return supplier.get();
      }

      @Override
      public Optional<SUBJECT> optional() {
        return Optional.of(supplier.get());
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
      public Violations violations() {
        return Violations.create();
      }
    };
  }

  static <SUBJECT> Valid<SUBJECT> invalid(Violations violations) {
    if (violations.isEmpty()) {
      throw new IllegalArgumentException("Violations must not be empty");
    }
    return new Valid<>() {

      @Override
      public <NEW_SUBJECT> Valid<NEW_SUBJECT> map(Function<SUBJECT, NEW_SUBJECT> mapper) {
        return self();
      }

      @Override
      public <NEW_SUBJECT> Valid<NEW_SUBJECT> flatMap(Function<SUBJECT, Valid<NEW_SUBJECT>> mapper) {
        return self();
      }

      @Override
      public SUBJECT orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
        throw Internals.hide(exceptionSupplier.get());
      }

      @Override
      public SUBJECT orElseThrow(Function<Violations, ? extends Throwable> exceptionFn) {
        throw Internals.hide(exceptionFn.apply(violations));
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
      public Violations violations() {
        return violations;
      }

      private <MIRROR> Valid<MIRROR> self() {
        @SuppressWarnings("unchecked")
        var self = (Valid<MIRROR>) this;
        return self;
      }
    };
  }

  static <SUBJECT> ValidationSpec<SUBJECT> validationSpec(
      Function<RootAssertionsSpec<SUBJECT, SUBJECT>, RootAssertionsSpec<SUBJECT, SUBJECT>> specAction) {
    return new RulesValidationSpec<>(
        specAction.apply(
            null
//            AssertionsSpec.from(
//                Rules.empty(),
//                Function.identity()
//            )
        ).rules()
    );
  }
}
