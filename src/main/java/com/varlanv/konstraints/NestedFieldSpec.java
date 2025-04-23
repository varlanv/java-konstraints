package com.varlanv.konstraints;

import java.util.function.Function;

public interface NestedFieldSpec<PARENT, ROOT, CURRENT> extends BaseFieldSpec<ROOT, CURRENT> {

  @Override
  <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

  @Override
  <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

  @Override
  NestedNullSpec<PARENT, ROOT, CURRENT> nonNull();

  @Override
  NestedNullSpec<PARENT, ROOT, CURRENT> nullable();

  @Override
  String fieldName();

  @Override
  NestedAssertionsSpec<PARENT, ROOT, CURRENT> parent();
}
