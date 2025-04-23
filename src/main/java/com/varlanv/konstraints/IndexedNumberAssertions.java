package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface IndexedNumberAssertions<PARENT, TARGET extends Number & Comparable<TARGET>, ROOT, CURRENT>
    extends NumberAssertions<TARGET, ROOT, CURRENT> {

  IndexedNumberAssertions<PARENT, TARGET, ROOT, CURRENT> assertCustom(BiPredicateIndexed<@NotNull TARGET, PARENT> action);

  IndexedNumberAssertions<PARENT, TARGET, ROOT, CURRENT> assertCustom(PredicateIndexed<@NotNull TARGET> action);

  @Override
  IndexedNumberAssertions<PARENT, TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  @Override
  IndexedNumberAssertions<PARENT, TARGET, ROOT, CURRENT> assertGte(TARGET target);

  @Override
  IndexedNumberAssertions<PARENT, TARGET, ROOT, CURRENT> assertLte(TARGET target);

  @Override
  IndexedNumberAssertions<PARENT, TARGET, ROOT, CURRENT> assertInRange(TARGET minTarget, TARGET maxTarget);
}
