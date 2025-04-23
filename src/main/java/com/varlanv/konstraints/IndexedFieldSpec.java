package com.varlanv.konstraints;

import java.util.function.Function;

public interface IndexedFieldSpec<PARENT, ROOT, CURRENT> {

  <TARGET> IndexedAssertionsSpec<PARENT, ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

  <TARGET> IndexedAssertionsSpec<PARENT, ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

  IndexedNullSpec<PARENT, ROOT, CURRENT> nonNull();

  IndexedNullSpec<PARENT, ROOT, CURRENT> nullable();

  String fieldName();

  IndexedAssertionsSpec<PARENT, ROOT, CURRENT> parent();
}
