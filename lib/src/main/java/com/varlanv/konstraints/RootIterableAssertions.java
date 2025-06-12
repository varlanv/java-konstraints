package com.varlanv.konstraints;

import java.util.function.Function;

public final class RootIterableAssertions<
    ASSERTIONS extends BaseAssertionsSpec<ROOT, ITEM>,
    ITEM,
    ITEMS extends Iterable<ITEM>,
    ROOT,
    CURRENT> {

  public ASSERTIONS assertions() {
    return null;
  }

  public RootIterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertSize(
      Integer size) {
    return null;
  }

  public RootIterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertMinSize(
      Integer minSize) {
    return null;
  }

  public RootIterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertMaxSize(
      Integer maxSize) {
    return null;
  }

  public RootIterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertSizeRange(
      Integer minSize, Integer maxSize) {
    return null;
  }

  public RootIterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertNotEmpty() {
    return null;
  }

  public RootIterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertEmpty() {
    return null;
  }

  public RootIterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> eachItem(
      Function<ASSERTIONS, ASSERTIONS> action) {
    return null;
  }
}
