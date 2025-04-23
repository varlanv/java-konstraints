package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface NullSpec<ROOT, CURRENT> extends BaseNullSpec<ROOT, CURRENT> {

  <TARGET> AssertionsSpec<ROOT, CURRENT> custom(Function<@NotNull CURRENT, @Nullable TARGET> extract,
                                                Function<@NotNull CustomAssertions<TARGET, ROOT, CURRENT>,
                                                    @NotNull CustomAssertions<TARGET, ROOT, CURRENT>> action);

  <TARGET extends CharSequence,
      ASSERTIONS extends BaseStringAssertions<TARGET, ROOT, CURRENT>> AssertionsSpec<ROOT, CURRENT> string(
      Function<@NotNull CURRENT, @Nullable String> extract,
      Function<@NotNull ASSERTIONS, @NotNull ASSERTIONS> action);

  <TARGET extends Number & Comparable<TARGET>,
      ASSERTIONS extends BaseNumberAssertions<TARGET, ROOT, CURRENT>> AssertionsSpec<ROOT, CURRENT> number(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull ASSERTIONS,
          @NotNull ASSERTIONS> action);

  <TARGET, ASSERTIONS extends NestedAssertionsSpec<Child<TARGET, CURRENT>, CURRENT, TARGET>> AssertionsSpec<ROOT, CURRENT> nested(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull ASSERTIONS, @NotNull ASSERTIONS> action);

  FieldSpec<ROOT, CURRENT> parent();

  IterableSpec<CURRENT, ROOT, CURRENT> iterable();

  Boolean allowNull();
}
