package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface NumberAssertions<TARGET extends Comparable<TARGET>, ROOT, CURRENT> {

  Rules<ROOT> rules();

  NumberAssertions<TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  NumberAssertions<TARGET, ROOT, CURRENT> assertGte(TARGET target);

  NumberAssertions<TARGET, ROOT, CURRENT> assertLte(TARGET target);

  NumberAssertions<TARGET, ROOT, CURRENT> assertInRange(TARGET minTarget, TARGET maxTarget);

  static <TARGET extends Number & Comparable<TARGET>, ROOT, CURRENT> NumberAssertions<TARGET, ROOT, CURRENT> of(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Rules<ROOT> rules,
      NullSpec<ROOT, CURRENT> parent) {
    return null;
  }
}
