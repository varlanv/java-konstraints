package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface BaseNumberAssertions<TARGET extends Comparable<TARGET>, ROOT, CURRENT> {

  Rules<ROOT> rules();

  BaseNumberAssertions<TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  BaseNumberAssertions<TARGET, ROOT, CURRENT> assertGte(TARGET target);

  BaseNumberAssertions<TARGET, ROOT, CURRENT> assertLte(TARGET target);

  BaseNumberAssertions<TARGET, ROOT, CURRENT> assertInRange(TARGET minTarget, TARGET maxTarget);

  static <TARGET extends Number & Comparable<TARGET>, ROOT, CURRENT> BaseNumberAssertions<TARGET, ROOT, CURRENT> of(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Rules<ROOT> rules,
      NullSpec<ROOT, CURRENT> parent) {
    return null;
  }
}
