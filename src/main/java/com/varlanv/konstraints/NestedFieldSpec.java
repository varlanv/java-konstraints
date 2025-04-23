package com.varlanv.konstraints;

import java.util.function.Function;

public interface NestedFieldSpec<PARENT, ROOT, CURRENT> {

  <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

  <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

  NestedNullSpec<PARENT, ROOT, CURRENT> nonNull();

  NestedNullSpec<PARENT, ROOT, CURRENT> nullable();

  String fieldName();

  NestedAssertionsSpec<PARENT, ROOT, CURRENT> parent();
}
