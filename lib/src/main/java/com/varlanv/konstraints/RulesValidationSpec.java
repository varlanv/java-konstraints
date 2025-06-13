package com.varlanv.konstraints;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

final class RulesValidationSpec<SUBJECT> implements ValidationSpec<SUBJECT> {

    private final Rules<SUBJECT> rules;

    RulesValidationSpec(Rules<SUBJECT> rules) {
        this.rules = rules;
    }

    @Override
    public Function<SUBJECT, Valid<SUBJECT>> toFunction() {
        return this::validate;
    }

    @Override
    public UnaryOperator<SUBJECT> toFailingValidationOperator(Function<Violations, ? extends Throwable> onException) {
        Objects.requireNonNull(onException, "nulls are not supported");
        return t -> validate(t).orElseThrow(onException);
    }

    @Override
    public UnaryOperator<SUBJECT> toThrowingOperator(Supplier<? extends Throwable> onException) {
        Objects.requireNonNull(onException, "nulls are not supported");
        return t -> validate(t).orElseThrow(onException);
    }

    @Override
    public Valid<SUBJECT> validate(SUBJECT t) {
        Objects.requireNonNull(t, "nulls are not supported");
        var violations = rules.apply(Objects.requireNonNull(t));
        if (violations.isEmpty()) {
            return Valid.valid(t);
        } else {
            return Valid.invalid(violations);
        }
    }
}
