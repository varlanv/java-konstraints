package com.varlanv.konstraints;

import java.util.function.Function;

public interface BaseFieldSpec<ROOT, CURRENT> {

  <TARGET> BaseAssertionsSpec<ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

  <TARGET> BaseAssertionsSpec<ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

  BaseNullSpec<ROOT, CURRENT> nonNull();

  BaseNullSpec<ROOT, CURRENT> nullable();

  String fieldName();

  BaseAssertionsSpec<ROOT, CURRENT> parent();
}
