package com.varlanv.konstraints;

@FunctionalInterface
public interface IndexFunction<A, B> {

    B apply(A a, int index);
}
