package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface AssertionsSpec<ROOT, CURRENT> {

  FieldSpec<ROOT, CURRENT> field(String fieldName);

  AssertionsSpec<ROOT, CURRENT> withRule(Rule<ROOT> rule);

  AssertionsSpec<ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

  Rules<ROOT> rules();

  Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();

  static <ROOT, CURRENT> AssertionsSpec<ROOT, CURRENT> from(
      Rules<ROOT> rules, Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn) {
    return null;
  }
}
