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
    public Function<SUBJECT, Valid<SUBJECT>> toValidationFunction() {
        return this::validate;
    }

    @Override
    public UnaryOperator<SUBJECT> toValidationFunction(Function<Violations, ? extends Throwable> onException) {
        Objects.requireNonNull(onException);
        return t -> validate(t).orElseThrow(onException);
    }

    @Override
    public UnaryOperator<SUBJECT> toValidationFunction(Supplier<? extends Throwable> onException) {
        Objects.requireNonNull(onException);
        return t -> validate(t).orElseThrow(onException);
    }

    @Override
    public Valid<SUBJECT> validate(SUBJECT t) {
        var violations = rules.apply(Objects.requireNonNull(t));
        if (violations.isEmpty()) {
            return Valid.valid(t);
        } else {
            return Valid.invalid(violations);
        }
    }
}
