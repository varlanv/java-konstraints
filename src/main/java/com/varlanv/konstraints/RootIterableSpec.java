package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class RootIterableSpec<ROOT, CURRENT> extends BaseIterableSpec<ROOT, CURRENT> {

  @Override
  RootNullSpec<ROOT, CURRENT> parent() {
    return null;
  }

  public <ITEM extends CharSequence, ITEMS extends Iterable<ITEM>>
  RootAssertionsSpec<ROOT, CURRENT> strings(
      Function<@NotNull CURRENT, @Nullable ITEMS> extract,
      Function<@NotNull RootStringIterableAssertions<ITEM, ITEMS, CURRENT, ROOT>,
          @NotNull RootStringIterableAssertions<ITEM, ITEMS, CURRENT, ROOT>> action) {
    return null;
  }
}
