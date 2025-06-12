package com.varlanv.konstraints;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

abstract class BaseCustomAssertions<TARGET, ROOT, CURRENT> extends RulesSpec<ROOT> {

    abstract BaseCustomAssertions<TARGET, ROOT, CURRENT> assertTrue(Function<@NotNull TARGET, @NotNull Boolean> action);

    abstract BaseNullSpec<ROOT, CURRENT> parent();
}
