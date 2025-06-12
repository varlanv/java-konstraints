package com.varlanv.konstraints;

@FunctionalInterface
public interface IndexBiFunction<A, B, C> {

    C apply(A a, B b, int index);
}
