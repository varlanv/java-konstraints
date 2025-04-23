package com.varlanv.konstraints;

public interface IterableAssertionsSpec<PARENT, ROOT, CURRENT> extends BaseAssertionsSpec<ROOT, CURRENT> {

  @Override
  NestedFieldSpec<PARENT, ROOT, CURRENT> field(String fieldName);

  @Override
  IterableAssertionsSpec<PARENT, ROOT, CURRENT> withRule(Rule<ROOT> rule);

  @Override
  IterableAssertionsSpec<PARENT, ROOT, CURRENT> mergeRules(Rules<ROOT> rules);
}
