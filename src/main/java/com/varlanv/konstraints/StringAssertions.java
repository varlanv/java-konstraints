package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface StringAssertions<TARGET extends CharSequence, ROOT, CURRENT>
    extends BaseStringAssertions<TARGET, ROOT, CURRENT> {

  @Override
  Rules<ROOT> rules();

  @Override
  StringAssertions<TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  @Override
  StringAssertions<TARGET, ROOT, CURRENT> assertEmpty();

  @Override
  StringAssertions<TARGET, ROOT, CURRENT> assertNotEmpty();

  @Override
  StringAssertions<TARGET, ROOT, CURRENT> assertNotBlank();

  @Override
  StringAssertions<TARGET, ROOT, CURRENT> assertLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer length);

  @Override
  StringAssertions<TARGET, ROOT, CURRENT> assertMinLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer minLength);

  @Override
  StringAssertions<TARGET, ROOT, CURRENT> assertMaxLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  @Override
  StringAssertions<TARGET, ROOT, CURRENT> assertLengthRange(@Range(from = 0, to = Integer.MAX_VALUE) Integer minLength,
                                                            @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  @Override
  StringAssertions<TARGET, ROOT, CURRENT> assertMatches(Pattern pattern);
}
