package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IndexedAssertionsSpec<PARENT, ROOT, CURRENT> extends BaseAssertionsSpec<ROOT, CURRENT> {

  @Override
  IndexedFieldSpec<PARENT, ROOT, CURRENT> field(String fieldName);

  @Override
  IndexedAssertionsSpec<PARENT, ROOT, CURRENT> withRule(Rule<ROOT> rule);

  @Override
  IndexedAssertionsSpec<PARENT, ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

  @Override
  Rules<ROOT> rules();

  @Override
  Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();
}
