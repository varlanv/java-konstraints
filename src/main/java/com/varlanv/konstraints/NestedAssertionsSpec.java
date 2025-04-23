package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface NestedAssertionsSpec<PARENT, ROOT, CURRENT>  {

  NestedFieldSpec<PARENT, ROOT, CURRENT> field(String fieldName);

  NestedAssertionsSpec<PARENT, ROOT, CURRENT> withRule(Rule<ROOT> rule);

  NestedAssertionsSpec<PARENT, ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

  Rules<ROOT> rules();

  Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();

  PARENT parent();

  static <PARENT, ROOT, CURRENT> NestedAssertionsSpec<PARENT, ROOT, CURRENT> of(
      Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn,
      Rules<ROOT> rules,
      PARENT parent) {
    return null;
  }
}
