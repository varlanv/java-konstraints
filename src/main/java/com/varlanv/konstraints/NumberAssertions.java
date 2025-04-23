package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface NumberAssertions<TARGET extends Comparable<TARGET>, ROOT, CURRENT>
    extends BaseNumberAssertions<TARGET, ROOT, CURRENT> {

  @Override
  Rules<ROOT> rules();

  @Override
  NumberAssertions<TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  @Override
  NumberAssertions<TARGET, ROOT, CURRENT> assertGte(TARGET target);

  @Override
  NumberAssertions<TARGET, ROOT, CURRENT> assertLte(TARGET target);

  @Override
  NumberAssertions<TARGET, ROOT, CURRENT> assertInRange(TARGET minTarget, TARGET maxTarget);
}
