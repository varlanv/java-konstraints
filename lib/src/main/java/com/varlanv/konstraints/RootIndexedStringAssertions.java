package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class RootIndexedStringAssertions<TARGET extends CharSequence, ROOT, CURRENT>
    extends BaseStringAssertions<TARGET, ROOT, CURRENT> {

  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> custom(
      BiFunction<@NotNull String, TARGET, @NotNull Violation> violation,
      Predicate<@NotNull TARGET> action) {
    return null;
  }

  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> customIndexed(
      IndexBiFunction<@NotNull String, TARGET, @NotNull Violation> violation,
      IndexPredicate<@NotNull TARGET> action) {
    return null;
  }

  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> customContextual(
      IndexTriFunction<@NotNull String, TARGET, CURRENT, @NotNull Violation> violation,
      IndexBiPredicate<@NotNull TARGET, @NotNull CURRENT> action) {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> custom(
      Function<@NotNull String, @NotNull Violation> violation, Predicate<@NotNull TARGET> action) {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> custom(String message, Predicate<@NotNull TARGET> action) {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertEmpty() {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> notEmpty() {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> assertNotBlank() {
    return null;
  }

  @Override
  public RootIndexedStringAssertions<TARGET, ROOT, CURRENT> len(
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
