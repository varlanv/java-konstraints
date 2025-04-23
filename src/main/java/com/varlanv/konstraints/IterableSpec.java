package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IterableSpec<PARENT, ROOT, CURRENT> {

  <ITEM extends CharSequence, ITEMS extends Iterable<ITEM>>
  AssertionsSpec<ROOT, CURRENT> strings(
      Function<@NotNull CURRENT, @Nullable ITEMS> extract,
      Function<IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, IndexedStringAssertions<PARENT, ITEM, ROOT, CURRENT>>,
          BaseAssertionsSpec<?, ?>> action);

  <ITEM extends Number & Comparable<ITEM>, ITEMS extends Iterable<ITEM>>
  AssertionsSpec<ROOT, CURRENT> numbers(
      Function<@NotNull CURRENT, @Nullable ITEMS> extract,
      Function<@NotNull IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, IndexedNumberAssertions<PARENT, ITEM, ROOT, CURRENT>>,
          BaseAssertionsSpec<?, ?>> action
  );

  <ITEM, ITEMS extends Iterable<ITEM>>
  AssertionsSpec<ROOT, CURRENT> nested(
      Function<@NotNull CURRENT, @Nullable ITEMS> extract,
      Function<@NotNull IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, IndexedAssertionsSpec<Child<ITEM, CURRENT>, ROOT, ITEM>>,
          @NotNull BaseAssertionsSpec<?, ?>> action);

  NullSpec<ROOT, CURRENT> parent();
}
