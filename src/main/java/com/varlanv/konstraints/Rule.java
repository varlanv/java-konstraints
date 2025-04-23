package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public interface Rule<SUBJECT> extends Function<@NotNull SUBJECT, @NotNull Optional<Violation>> {
}
