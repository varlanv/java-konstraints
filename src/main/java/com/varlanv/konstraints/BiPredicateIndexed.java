package com.varlanv.konstraints;

public interface BiPredicateIndexed<A, B> {

  boolean test(A a, B b, int index);
}
