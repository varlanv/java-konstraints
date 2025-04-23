package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNullByDefault;

import java.util.function.Function;

public interface FieldSpec<ROOT, CURRENT> {

  <TARGET> AssertionsSpec<ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

  <TARGET> AssertionsSpec<ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

  NullSpec<ROOT, CURRENT> nonNull();

  NullSpec<ROOT, CURRENT> nullable();

  String fieldName();

  AssertionsSpec<ROOT, CURRENT> parent();

  static <ROOT, CURRENT> FieldSpec<ROOT, CURRENT> of(
      String fieldName, AssertionsSpec<ROOT, CURRENT> parent) {
    return null;
  }
}
