package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

@FunctionalInterface
interface Rule<SUBJECT> extends BiFunction<@NotNull SUBJECT, Violations, @NotNull Violations> {
}
