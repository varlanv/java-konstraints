package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface NestedNullSpec<PARENT, ROOT, CURRENT> extends BaseNullSpec<ROOT, CURRENT> {

  <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> custom(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT>,
          @NotNull NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT>> action);

  <TARGET extends CharSequence> NestedAssertionsSpec<PARENT, ROOT, CURRENT> string(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull StringAssertions<TARGET, ROOT, CURRENT>,
          @NotNull StringAssertions<TARGET, ROOT, CURRENT>> action);

  <TARGET extends Number & Comparable<TARGET>> NestedAssertionsSpec<PARENT, ROOT, CURRENT> number(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull NumberAssertions<TARGET, ROOT, CURRENT>,
          @NotNull NumberAssertions<TARGET, ROOT, CURRENT>> action);

  <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> nested(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull NestedAssertionsSpec<Child<TARGET, PARENT>, CURRENT, TARGET>,
          @NotNull RulesSpec<CURRENT>> action);

  @Override
  NestedFieldSpec<PARENT, ROOT, CURRENT> parent();

  @Override
  IterableSpec<CURRENT, ROOT, CURRENT> iterable();
}
