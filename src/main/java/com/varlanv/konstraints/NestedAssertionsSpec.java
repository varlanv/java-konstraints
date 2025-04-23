package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface NestedAssertionsSpec<PARENT, ROOT, CURRENT> extends BaseAssertionsSpec<ROOT, CURRENT> {

  @Override
  NestedFieldSpec<PARENT, ROOT, CURRENT> field(String fieldName);

  @Override
  NestedAssertionsSpec<PARENT, ROOT, CURRENT> withRule(Rule<ROOT> rule);

  @Override
  NestedAssertionsSpec<PARENT, ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

  @Override
  Rules<ROOT> rules();

  @Override
  Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();
}
