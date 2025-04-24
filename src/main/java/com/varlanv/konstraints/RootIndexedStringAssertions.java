package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class RootIndexedStringAssertions<TARGET extends CharSequence, ROOT, CURRENT>
    extends BaseStringAssertions<TARGET, ROOT, CURRENT> {

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertCustom(
      Function<@NotNull String, @NotNull String> message, Predicate<@NotNull TARGET> action) {
    return null;
  }

  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertCustomWithIndex(
      Function<@NotNull String, @NotNull String> message, IndexPredicate<@NotNull TARGET> action) {
    return null;
  }

  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertCustomWithParent(
      Function<@NotNull String, @NotNull String> message, IndexBiPredicate<@NotNull TARGET, @NotNull CURRENT> action) {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertEmpty() {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertNotEmpty() {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertNotBlank() {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertLength(
      @Range(from = 1, to = Integer.MAX_VALUE) Integer length) {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertMinLength(
      @Range(from = 1, to = Integer.MAX_VALUE) Integer minLength) {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertMaxLength(
      @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength) {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertLengthRange(
      @Range(from = 0, to = Integer.MAX_VALUE) Integer minLength, @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength) {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertMatches(Pattern pattern) {
    return null;
  }

  @Override
  Rules<ROOT> rules() {
    return null;
  }
}
