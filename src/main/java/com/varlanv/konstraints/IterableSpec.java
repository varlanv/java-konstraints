package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IterableSpec<PARENT, ROOT, CURRENT> {


  <ITEM extends CharSequence, ITEMS extends Iterable<ITEM>>
  AssertionsSpec<ROOT, CURRENT> strings(
      Function<@NotNull CURRENT, @Nullable ITEMS> extract,
      Function<IterableAssertions<IndexedStringAssertions<PARENT, ITEM, ROOT, CURRENT>, ITEM, ITEMS, ROOT, CURRENT>,
          IterableAssertions<IndexedStringAssertions<PARENT, ITEM, ROOT, CURRENT>, ITEM, ITEMS, ROOT, CURRENT>> action);

  <ITEM extends Number & Comparable<ITEM>, ITEMS extends Iterable<ITEM>>
  AssertionsSpec<ROOT, CURRENT> numbers(
      Function<@NotNull CURRENT, @Nullable ITEMS> extract,
      Function<@NotNull IterableAssertions<IndexedNumberAssertions<PARENT, ITEM, ROOT, CURRENT>, ITEM, ITEMS, ROOT, CURRENT>,
          IterableAssertions<IndexedNumberAssertions<PARENT, ITEM, ROOT, CURRENT>, ITEM, ITEMS, ROOT, CURRENT>> action
  );

  <ITEM, ITEMS extends Iterable<ITEM>>
  AssertionsSpec<ROOT, CURRENT> nested(
      Function<@NotNull CURRENT, @Nullable ITEMS> extract,
      Function<@NotNull IterableAssertions<IndexedAssertionsSpec<Child<ITEM, CURRENT>, ROOT, ITEM>, ITEM, ITEMS, ROOT, CURRENT>,
          @NotNull IterableAssertions<IndexedAssertionsSpec<Child<ITEM, CURRENT>, ROOT, ITEM>, ITEM, ITEMS, ROOT, CURRENT>> action);

  NullSpec<ROOT, CURRENT> parent();
}
