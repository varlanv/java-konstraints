package com.varlanv.konstraints;

import java.util.function.Function;

public final class RootStringIterableAssertions<
    ITEM extends CharSequence, ITEMS extends Iterable<ITEM>, ROOT, CURRENT> {


  public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> assertSize(
      Integer size) {
    return null;
  }

  public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> assertMinSize(
      Integer minSize) {
    return null;
  }

  public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> assertMaxSize(
      Integer maxSize) {
    return null;
  }

  public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> assertSizeRange(
      Integer minSize, Integer maxSize) {
    return null;
  }

  public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> assertNotEmpty() {
    return null;
  }

  public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> assertEmpty() {
    return null;
  }

  public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> eachItem(
      Function<RootIndexedStringAssertions<ITEM, ROOT, CURRENT>,
          RootIndexedStringAssertions<ITEM, ROOT, CURRENT>> action) {
    return null;
  }
}
