package com.varlanv.konstraints;

import java.util.function.Function;

public interface IterableAssertions<ITEM, ITEMS extends
        Iterable<ITEM>, ROOT, CURRENT, ASSERTIONS extends BaseAssertionsSpec<?, ?>> extends BaseAssertionsSpec<ROOT, CURRENT> {

    ASSERTIONS assertions();

    IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, ASSERTIONS> assertSize(Integer size);

    IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, ASSERTIONS> assertMinSize(Integer minSize);

    IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, ASSERTIONS> assertMaxSize(Integer maxSize);

    IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, ASSERTIONS> assertSizeRange(Integer minSize, Integer maxSize);

    IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, ASSERTIONS> assertNotEmpty();

    IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, ASSERTIONS> assertEmpty();

    IterableAssertions<ITEM, ITEMS, ROOT, CURRENT, ASSERTIONS> eachItem(Function<ASSERTIONS, RulesSpec<ROOT>> action);
}
