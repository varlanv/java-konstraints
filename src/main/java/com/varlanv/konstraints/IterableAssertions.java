package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IterableAssertions<ASSERTIONS, ITEM, ITEMS extends Iterable<ITEM>, ROOT, CURRENT> {

  IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertSize(Integer size);

  IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertMinSize(Integer minSize);

  IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertMaxSize(Integer maxSize);

  IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertSizeRange(Integer minSize, Integer maxSize);

  IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertNotEmpty();

  IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertEmpty();

  IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> eachItem(Function<ASSERTIONS, ASSERTIONS> action);

  static <
      ASSERTIONS,
      ITEM,
      ITEMS extends Iterable<ITEM>,
      ROOT,
      CURRENT> IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> of(
      Function<@NotNull CURRENT, @Nullable ITEMS> extract, IterableSpec<CURRENT, ROOT, CURRENT> parent) {
    return null;
  }
}
