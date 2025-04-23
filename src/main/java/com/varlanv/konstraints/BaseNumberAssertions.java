package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface BaseNumberAssertions<TARGET extends Comparable<TARGET>, ROOT, CURRENT>
    extends BaseAssertionsSpec<ROOT, CURRENT> {

  Rules<ROOT> rules();

  BaseNumberAssertions<TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  BaseNumberAssertions<TARGET, ROOT, CURRENT> assertGte(TARGET target);

  BaseNumberAssertions<TARGET, ROOT, CURRENT> assertLte(TARGET target);

  BaseNumberAssertions<TARGET, ROOT, CURRENT> assertInRange(TARGET minTarget, TARGET maxTarget);
}
