package com.varlanv.konstraints;

@FunctionalInterface
public interface IndexTriFunction<A, B, C, D> {

  D apply(A a, B b, C c, int index);
}
