package com.varlanv.konstraints;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface AssertionsOperator<SUBJECT> extends UnaryOperator<AssertionsSpec<SUBJECT>> {}
