package com.varlanv.konstraints;

import java.util.function.Function;

abstract class BaseFieldSpec<ROOT, CURRENT> {

  public abstract <TARGET> BaseAssertionsSpec<ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

  public abstract <TARGET> BaseAssertionsSpec<ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

  public abstract BaseNullSpec<ROOT, CURRENT> nonNull();

  public abstract BaseNullSpec<ROOT, CURRENT> nullable();

  abstract String fieldName();

  abstract BaseAssertionsSpec<ROOT, CURRENT> parent();
}
