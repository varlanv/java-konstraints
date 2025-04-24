package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class RootNumberAssertions<TARGET extends Comparable<TARGET>, ROOT, CURRENT>
    extends BaseNumberAssertions<TARGET, ROOT, CURRENT> {

  @Override
  public RootNumberAssertions<TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action) {
    return null;
  }

  public RootNumberAssertions<TARGET, ROOT, CURRENT> assertCustom(BiPredicate<@NotNull TARGET, CURRENT> action) {
    return null;
  }

  @Override
  public RootNumberAssertions<TARGET, ROOT, CURRENT> assertGte(TARGET target) {
    return null;
  }

  @Override
  public RootNumberAssertions<TARGET, ROOT, CURRENT> assertLte(TARGET target) {
    return null;
  }

  @Override
  public RootNumberAssertions<TARGET, ROOT, CURRENT> assertInRange(TARGET minTarget, TARGET maxTarget) {
    return null;
  }

  RootAssertionsSpec<ROOT, CURRENT> parent() {
    return null;
  }

  @Override
  Rules<ROOT> rules() {
    return null;
  }
}
