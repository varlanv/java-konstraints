package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class RootNullSpec<ROOT, CURRENT> extends BaseNullSpec<ROOT, CURRENT> {

  public <TARGET extends CharSequence> RootAssertionsSpec<ROOT, CURRENT> string(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull RootStringAssertions<TARGET, ROOT, CURRENT>,
          @NotNull RootStringAssertions<TARGET, ROOT, CURRENT>> action) {
    return null;
  }

  public <TARGET extends Number & Comparable<TARGET>> RootAssertionsSpec<ROOT, CURRENT> number(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull RootNumberAssertions<TARGET, ROOT, CURRENT>,
          @NotNull RootNumberAssertions<TARGET, ROOT, CURRENT>> action) {
    return null;
  }

  <TARGET> RootAssertionsSpec<ROOT, CURRENT> nested(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull NestedAssertionsSpec<Child<TARGET, CURRENT>, CURRENT, TARGET>,
          @NotNull RulesSpec<ROOT>> action) {
    return null;
  }

  RootFieldSpec<ROOT, CURRENT> parent() {
    return null;
  }

  public RootIterableSpec<ROOT, CURRENT> iterable() {
    return null;
  }

  @Override
  Boolean allowNull() {
    return null;
  }
}
