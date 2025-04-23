package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IndexedAssertionsSpec<PARENT, ROOT, CURRENT>  {

  IndexedFieldSpec<PARENT, ROOT, CURRENT> field(String fieldName);

  IndexedAssertionsSpec<PARENT, ROOT, CURRENT> withRule(Rule<ROOT> rule);

  IndexedAssertionsSpec<PARENT, ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

  Rules<ROOT> rules();

  Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();

  PARENT parent();

  static <PARENT, ROOT, CURRENT> IndexedAssertionsSpec<PARENT, ROOT, CURRENT> of(
      Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn,
      Rules<ROOT> rules,
      PARENT parent) {
    return null;
  }
}
