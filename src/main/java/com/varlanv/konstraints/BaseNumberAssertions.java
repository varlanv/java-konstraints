package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

abstract class BaseNumberAssertions<TARGET extends Comparable<TARGET>, ROOT, CURRENT>
    extends RulesSpec<ROOT> {

  public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> assertCustom(
      Function<@NotNull String, @NotNull Violation> violation,
      Predicate<@NotNull TARGET> action);

  public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> assertCustom(
      String message,
      Predicate<@NotNull TARGET> action);

  public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> assertGte(TARGET target);

  public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> assertLte(TARGET target);

  public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> assertInRange(TARGET minTarget, TARGET maxTarget);
}
