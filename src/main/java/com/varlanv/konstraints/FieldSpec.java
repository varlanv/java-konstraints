package com.varlanv.konstraints;

import java.util.function.Function;

public interface FieldSpec<ROOT, CURRENT> extends BaseFieldSpec<ROOT, CURRENT> {

  @Override
  <TARGET> AssertionsSpec<ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

  @Override
  <TARGET> AssertionsSpec<ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

  @Override
  NullSpec<ROOT, CURRENT> nonNull();

  @Override
  NullSpec<ROOT, CURRENT> nullable();

  @Override
  AssertionsSpec<ROOT, CURRENT> parent();
}
