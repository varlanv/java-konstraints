package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IndexedNullSpec<PARENT, ROOT, CURRENT> extends BaseNullSpec<ROOT, CURRENT> {

  <TARGET> IndexedAssertionsSpec<PARENT, ROOT, CURRENT> custom(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull IndexedCustomAssertions<TARGET, PARENT, ROOT, CURRENT>,
          @NotNull IndexedCustomAssertions<TARGET, PARENT, ROOT, CURRENT>> action);

  <TARGET extends CharSequence> IndexedAssertionsSpec<PARENT, ROOT, CURRENT> string(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull IndexedStringAssertions<CURRENT, TARGET, ROOT, CURRENT>,
          @NotNull IndexedStringAssertions<CURRENT, TARGET, ROOT, CURRENT>> action);

  <TARGET extends Number & Comparable<TARGET>> IndexedAssertionsSpec<PARENT, ROOT, CURRENT> number(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull IndexedNumberAssertions<PARENT, TARGET, ROOT, CURRENT>,
          @NotNull IndexedNumberAssertions<PARENT, TARGET, ROOT, CURRENT>> action);

  <TARGET> IndexedAssertionsSpec<PARENT, ROOT, CURRENT> nested(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull IndexedAssertionsSpec<Child<TARGET, PARENT>, CURRENT, TARGET>,
          @NotNull IndexedAssertionsSpec<Child<TARGET, PARENT>, CURRENT, TARGET>> action);

  @Override
  NestedFieldSpec<PARENT, ROOT, CURRENT> parent();

  @Override
  IterableSpec<CURRENT, ROOT, CURRENT> iterable();

  @Override
  Boolean allowNull();
}
