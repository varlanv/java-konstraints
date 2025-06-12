package com.varlanv.konstraints;

@FunctionalInterface
public interface IndexBiPredicate<A, B> {

    boolean test(A a, B b, int index);
}
