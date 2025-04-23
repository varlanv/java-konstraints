package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IterableAssertionsSpec<PARENT, ROOT, CURRENT>  {

  NestedFieldSpec<PARENT, ROOT, CURRENT> field(String fieldName);

  IterableAssertionsSpec<PARENT, ROOT, CURRENT> withRule(Rule<ROOT> rule);

  IterableAssertionsSpec<PARENT, ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

  Rules<ROOT> rules();

  Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();

  PARENT parent();

  static <PARENT, ROOT, CURRENT> IterableAssertionsSpec<PARENT, ROOT, CURRENT> of(
      Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn,
      Rules<ROOT> rules,
      PARENT parent) {
    return null;
  }
}
