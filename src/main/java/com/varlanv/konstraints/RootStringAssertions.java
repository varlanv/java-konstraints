package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class RootStringAssertions<TARGET extends CharSequence, ROOT, CURRENT>
    extends BaseStringAssertions<TARGET, ROOT, CURRENT> {

  @Override
  Rules<ROOT> rules() {
    return null;
  }

  @Override
  public RootStringAssertions<TARGET, ROOT, CURRENT> assertCustom(
      Function<@NotNull String, @NotNull Violation> message, Predicate<@NotNull TARGET> action) {
    return null;
  }

  public RootStringAssertions<TARGET, ROOT, CURRENT> assertCustom(
      Function<@NotNull String, @NotNull Violation> message, BiPredicate<@NotNull TARGET, @NotNull CURRENT> action) {
    return null;
  }

  @Override
  public RootStringAssertions<TARGET, ROOT, CURRENT> assertEmpty() {
    return null;
  }

  @Override
  public RootStringAssertions<TARGET, ROOT, CURRENT> assertNotEmpty() {
    return null;
  }

  @Override
  public RootStringAssertions<TARGET, ROOT, CURRENT> assertNotBlank() {
    return null;
  }

  @Override
  public RootStringAssertions<TARGET, ROOT, CURRENT> assertLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer length) {
    return null;
  }

  @Override
  public RootStringAssertions<TARGET, ROOT, CURRENT> assertMinLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer minLength) {
    return null;
  }

  @Override
  public RootStringAssertions<TARGET, ROOT, CURRENT> assertMaxLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength) {
    return null;
  }

  @Override
  public RootStringAssertions<TARGET, ROOT, CURRENT> assertLengthRange(@Range(from = 0, to = Integer.MAX_VALUE) Integer minLength,
                                                                       @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength) {
    return null;
  }

  @Override
  public RootStringAssertions<TARGET, ROOT, CURRENT> assertMatches(Pattern pattern) {
    return null;
  }
}
