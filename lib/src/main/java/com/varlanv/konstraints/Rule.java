package com.varlanv.konstraints;

import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
interface Rule<SUBJECT> extends BiFunction<@NotNull SUBJECT, Violations, @NotNull Violations> {}
