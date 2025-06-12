package com.varlanv.konstraints;

@FunctionalInterface
public interface IndexPredicate<A> {

  boolean test(A a, int index);
}
