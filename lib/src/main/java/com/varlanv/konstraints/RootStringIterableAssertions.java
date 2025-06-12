package com.varlanv.konstraints;

import java.util.function.Function;

public final class RootStringIterableAssertions<
        ITEM extends CharSequence, ITEMS extends Iterable<ITEM>, ROOT, CURRENT> {

    public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> size(Integer size) {
        return null;
    }

    public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> minSize(Integer minSize) {
        return null;
    }

    public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> maxSize(Integer maxSize) {
        return null;
    }

    public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> sizeRange(Integer minSize, Integer maxSize) {
        return null;
    }

    public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> notEmpty() {
        return null;
    }

    public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> empty() {
        return null;
    }

    public RootStringIterableAssertions<ITEM, ITEMS, ROOT, CURRENT> eachItem(
            Function<RootIndexedStringAssertions<ITEM, ROOT, CURRENT>, RootIndexedStringAssertions<ITEM, ROOT, CURRENT>>
                    action) {
        return null;
    }
}
