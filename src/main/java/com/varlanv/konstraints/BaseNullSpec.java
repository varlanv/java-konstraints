package com.varlanv.konstraints;

public interface BaseNullSpec<ROOT, CURRENT> {

  BaseFieldSpec<ROOT, CURRENT> parent();

  IterableSpec<CURRENT, ROOT, CURRENT> iterable();

  Boolean allowNull();
}
