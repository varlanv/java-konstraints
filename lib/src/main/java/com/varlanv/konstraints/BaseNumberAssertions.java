package com.varlanv.konstraints;

import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

abstract class BaseNumberAssertions<TARGET extends Comparable<TARGET>, ROOT, CURRENT> extends RulesSpec<ROOT> {

    public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> check(
            Function<@NotNull String, @NotNull Violation> violation, Predicate<@NotNull TARGET> action);

    public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> check(
            String message, Predicate<@NotNull TARGET> action);

    public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> assertGte(TARGET target);

    public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> lte(TARGET target);

    public abstract BaseNumberAssertions<TARGET, ROOT, CURRENT> assertInRange(TARGET minTarget, TARGET maxTarget);
}
