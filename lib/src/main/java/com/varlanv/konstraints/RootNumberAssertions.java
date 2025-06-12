package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public final class RootNumberAssertions<TARGET extends Comparable<TARGET>, ROOT, CURRENT>
    extends BaseNumberAssertions<TARGET, ROOT, CURRENT> {

  public RootNumberAssertions<TARGET, ROOT, CURRENT> customContextual(
      TriFunction<@NotNull String, TARGET, CURRENT, @NotNull Violation> violation,
      BiPredicate<@NotNull TARGET, @NotNull CURRENT> action) {
    return null;
  }

  public RootNumberAssertions<TARGET, ROOT, CURRENT> custom(Function<@NotNull String, @NotNull Violation> violation,
                                                            Predicate<@NotNull TARGET> action) {
    return null;
  }

  @Override
  public RootNumberAssertions<TARGET, ROOT, CURRENT> custom(String message, Predicate<@NotNull TARGET> action) {
    return null;
  }

  @Override
  public RootNumberAssertions<TARGET, ROOT, CURRENT> assertGte(TARGET target) {
    return null;
  }

  @Override
  public RootNumberAssertions<TARGET, ROOT, CURRENT> lte(TARGET target) {
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
