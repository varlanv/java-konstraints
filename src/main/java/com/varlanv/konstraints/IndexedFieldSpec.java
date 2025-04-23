package com.varlanv.konstraints;

import java.util.function.Function;

public interface IndexedFieldSpec<PARENT, ROOT, CURRENT> extends BaseFieldSpec<ROOT, CURRENT> {

  @Override
  <TARGET> IndexedAssertionsSpec<PARENT, ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

  @Override
  <TARGET> IndexedAssertionsSpec<PARENT, ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

  @Override
  IndexedNullSpec<PARENT, ROOT, CURRENT> nonNull();

  @Override
  IndexedNullSpec<PARENT, ROOT, CURRENT> nullable();

  @Override
  String fieldName();

  @Override
  IndexedAssertionsSpec<PARENT, ROOT, CURRENT> parent();
}
