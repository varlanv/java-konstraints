package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface BaseStringAssertions<TARGET extends CharSequence, ROOT, CURRENT> {

  Rules<ROOT> rules();

  BaseStringAssertions<TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  BaseStringAssertions<TARGET, ROOT, CURRENT> assertEmpty();

  BaseStringAssertions<TARGET, ROOT, CURRENT> assertNotEmpty();

  BaseStringAssertions<TARGET, ROOT, CURRENT> assertNotBlank();

  BaseStringAssertions<TARGET, ROOT, CURRENT> assertLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer length);

  BaseStringAssertions<TARGET, ROOT, CURRENT> assertMinLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer minLength);

  BaseStringAssertions<TARGET, ROOT, CURRENT> assertMaxLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  BaseStringAssertions<TARGET, ROOT, CURRENT> assertLengthRange(@Range(from = 0, to = Integer.MAX_VALUE) Integer minLength,
                                                                @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  BaseStringAssertions<TARGET, ROOT, CURRENT> assertMatches(Pattern pattern);
}
