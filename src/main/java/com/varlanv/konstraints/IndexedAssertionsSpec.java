package com.varlanv.konstraints;

public interface IndexedAssertionsSpec<PARENT, ROOT, CURRENT> extends BaseAssertionsSpec<ROOT, CURRENT> {

  @Override
  IndexedFieldSpec<PARENT, ROOT, CURRENT> field(String fieldName);

  @Override
  IndexedAssertionsSpec<PARENT, ROOT, CURRENT> withRule(Rule<ROOT> rule);

  @Override
  IndexedAssertionsSpec<PARENT, ROOT, CURRENT> mergeRules(Rules<ROOT> rules);
}
