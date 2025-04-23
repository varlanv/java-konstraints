package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface IndexedStringAssertions<PARENT, TARGET extends CharSequence, ROOT, CURRENT>
    extends BaseStringAssertions<TARGET, ROOT, CURRENT> {

  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertCustom(
      BiPredicateIndexed<@NotNull TARGET, @NotNull PARENT> action);

  @Override
  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  @Override
  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertEmpty();

  @Override
  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertNotEmpty();

  @Override
  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertNotBlank();

  @Override
  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertLength(
      @Range(from = 1, to = Integer.MAX_VALUE) Integer length);

  @Override
  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertMinLength(
      @Range(from = 1, to = Integer.MAX_VALUE) Integer minLength);

  @Override
  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertMaxLength(
      @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  @Override
  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertLengthRange(
      @Range(from = 0, to = Integer.MAX_VALUE) Integer minLength,
      @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  @Override
  IndexedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertMatches(Pattern pattern);
}
