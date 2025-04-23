package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface NullSpec<ROOT, CURRENT> extends BaseNullSpec<ROOT, CURRENT> {

  <TARGET> AssertionsSpec<ROOT, CURRENT> custom(Function<@NotNull CURRENT, @Nullable TARGET> extract,
                                                Function<@NotNull CustomAssertions<TARGET, ROOT, CURRENT>,
                                                    @NotNull CustomAssertions<TARGET, ROOT, CURRENT>> action);

  <TARGET extends CharSequence> AssertionsSpec<ROOT, CURRENT> string(
      Function<@NotNull CURRENT, @Nullable String> extract,
      Function<@NotNull StringAssertions<TARGET, ROOT, CURRENT>,
          @NotNull StringAssertions<TARGET, ROOT, CURRENT>> action);

  <TARGET extends Number & Comparable<TARGET>> AssertionsSpec<ROOT, CURRENT> number(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull NumberAssertions<TARGET, ROOT, CURRENT>,
          @NotNull NumberAssertions<TARGET, ROOT, CURRENT>> action);

  <TARGET> AssertionsSpec<ROOT, CURRENT> nested(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Function<@NotNull NestedAssertionsSpec<Child<TARGET, CURRENT>, CURRENT, TARGET>,
          @NotNull BaseAssertionsSpec<?, ?>> action);

  FieldSpec<ROOT, CURRENT> parent();

  IterableSpec<CURRENT, ROOT, CURRENT> iterable();
}
