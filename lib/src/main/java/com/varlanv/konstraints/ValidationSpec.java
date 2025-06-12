package com.varlanv.konstraints;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface ValidationSpec<SUBJECT> {

    Function<SUBJECT, Valid<SUBJECT>> toValidationFunction();

    UnaryOperator<SUBJECT> toValidationFunction(Function<Violations, ? extends Throwable> onException);

    UnaryOperator<SUBJECT> toValidationFunction(Supplier<? extends Throwable> onException);

    Valid<SUBJECT> validate(SUBJECT t);
}
