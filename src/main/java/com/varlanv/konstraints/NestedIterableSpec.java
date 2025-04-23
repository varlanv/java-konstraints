package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface NestedIterableSpec<PARENT, ROOT, CURRENT> {

    <ITEM extends CharSequence, ITEMS extends Iterable<ITEM>>
    NestedAssertionsSpec<PARENT, ROOT, CURRENT> strings(
            Function<@NotNull CURRENT, @Nullable ITEMS> extract,
            Function<IterableAssertions<ITEM, ITEMS, ROOT, CURRENT,
                    IndexedStringAssertions<PARENT, ITEM, ROOT, CURRENT>>, RulesSpec<ROOT>> action);

    <ITEM extends Number & Comparable<ITEM>, ITEMS extends Iterable<ITEM>>
    NestedAssertionsSpec<PARENT, ROOT, CURRENT> numbers(
            Function<@NotNull CURRENT, @Nullable ITEMS> extract,
            Function<@NotNull IterableAssertions<ITEM, ITEMS, ROOT, CURRENT,
                    IndexedNumberAssertions<PARENT, ITEM, ROOT, CURRENT>>, RulesSpec<ROOT>> action
    );

    <ITEM, ITEMS extends Iterable<ITEM>> NestedAssertionsSpec<PARENT, ROOT, CURRENT> nested(
            Function<@NotNull CURRENT, @Nullable ITEMS> extract,
            Function<@NotNull IterableAssertions<ITEM, ITEMS, ROOT, CURRENT,
                    IndexedAssertionsSpec<Child<ITEM, CURRENT>, ROOT, ITEM>>, @NotNull RulesSpec<ROOT>> action);

    NestedNullSpec<PARENT, ROOT, CURRENT> parent();
}
