package com.varlanv.konstraints;

public interface IndexBiPredicate<A, B> {

  boolean test(A a, B b, int index);
}
