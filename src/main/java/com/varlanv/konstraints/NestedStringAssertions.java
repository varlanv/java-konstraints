package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface NestedStringAssertions<PARENT, TARGET extends CharSequence, ROOT, CURRENT>
    extends BaseStringAssertions<TARGET, ROOT, CURRENT> {

  @Override
  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertCustom(
      BiPredicate<@NotNull TARGET, @NotNull PARENT> action);

  @Override
  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertEmpty();

  @Override
  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertNotEmpty();

  @Override
  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertNotBlank();

  @Override
  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertLength(
      @Range(from = 1, to = Integer.MAX_VALUE) Integer length);

  @Override
  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertMinLength(
      @Range(from = 1, to = Integer.MAX_VALUE) Integer minLength);

  @Override
  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertMaxLength(
      @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  @Override
  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertLengthRange(
      @Range(from = 0, to = Integer.MAX_VALUE) Integer minLength,
      @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  @Override
  NestedStringAssertions<PARENT, TARGET, ROOT, CURRENT> assertMatches(Pattern pattern);
}
