package com.varlanv.konstraints;

import java.util.function.Function;

public interface FieldSpec<ROOT, CURRENT> extends BaseFieldSpec<ROOT, CURRENT> {

  <TARGET> AssertionsSpec<ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

  <TARGET> AssertionsSpec<ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

  BaseNullSpec<ROOT, CURRENT> nonNull();

  BaseNullSpec<ROOT, CURRENT> nullable();

  String fieldName();

  AssertionsSpec<ROOT, CURRENT> parent();

  static <ROOT, CURRENT> FieldSpec<ROOT, CURRENT> of(
      String fieldName, AssertionsSpec<ROOT, CURRENT> parent) {
    return null;
  }
}
