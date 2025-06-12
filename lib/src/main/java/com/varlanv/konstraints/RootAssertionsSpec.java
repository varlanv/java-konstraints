package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class RootAssertionsSpec<ROOT, CURRENT> extends BaseAssertionsSpec<ROOT, CURRENT> {

  @Override
  public RootFieldSpec<ROOT, CURRENT> field(String fieldName) {
    return null;
  }

  @Override
  RootAssertionsSpec<ROOT, CURRENT> withRule(Rule<ROOT> rule) {
    return null;
  }

  @Override
  RootAssertionsSpec<ROOT, CURRENT> mergeRules(Rules<ROOT> rules) {
    return null;
  }

  @Override
  Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn() {
    return null;
  }

  @Override
  Rules<ROOT> rules() {
    return null;
  }
}
