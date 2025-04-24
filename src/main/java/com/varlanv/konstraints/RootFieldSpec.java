package com.varlanv.konstraints;

import java.util.function.Function;

public final class RootFieldSpec<ROOT, CURRENT> extends BaseFieldSpec<ROOT, CURRENT> {

  @Override
  public <TARGET> RootAssertionsSpec<ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract) {
    return null;
  }

  @Override
  public <TARGET> RootAssertionsSpec<ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract) {
    return null;
  }

  @Override
  public RootNullSpec<ROOT, CURRENT> nonNull() {
    return null;
  }

  @Override
  public RootNullSpec<ROOT, CURRENT> nullable() {
    return null;
  }

  @Override
  String fieldName() {
    return "";
  }

  @Override
  RootAssertionsSpec<ROOT, CURRENT> parent() {
    return null;
  }
}
