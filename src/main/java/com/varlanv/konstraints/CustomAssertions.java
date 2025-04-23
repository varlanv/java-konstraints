package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface CustomAssertions<TARGET, ROOT, CURRENT> {

  CustomAssertions<TARGET, ROOT, CURRENT> assertTrue(
      Function<@NotNull CURRENT, @Nullable TARGET> extract, Function<@NotNull TARGET, @NotNull Boolean> action);

  Rules<ROOT> rules();

  NullSpec<ROOT, CURRENT> parent();

  static <TARGET, ROOT, CURRENT> CustomAssertions<TARGET, ROOT, CURRENT> of(Function<@NotNull CURRENT, @Nullable TARGET> extract,
                                                                            Rules<ROOT> rules,
                                                                            NullSpec<ROOT, CURRENT> parent) {
    return null;
  }
}
