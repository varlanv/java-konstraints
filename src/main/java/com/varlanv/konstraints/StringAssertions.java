package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface StringAssertions<TARGET extends CharSequence, ROOT, CURRENT> {

  Rules<ROOT> rules();

  StringAssertions<TARGET, ROOT, CURRENT> assertCustom(Predicate<@NotNull TARGET> action);

  StringAssertions<TARGET, ROOT, CURRENT> assertEmpty();

  StringAssertions<TARGET, ROOT, CURRENT> assertNotEmpty();

  StringAssertions<TARGET, ROOT, CURRENT> assertNotBlank();

  StringAssertions<TARGET, ROOT, CURRENT> assertLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer length);

  StringAssertions<TARGET, ROOT, CURRENT> assertMinLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer minLength);

  StringAssertions<TARGET, ROOT, CURRENT> assertMaxLength(@Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  StringAssertions<TARGET, ROOT, CURRENT> assertLengthRange(@Range(from = 0, to = Integer.MAX_VALUE) Integer minLength,
                                                            @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

  StringAssertions<TARGET, ROOT, CURRENT> assertMatches(Pattern pattern);

  static <TARGET extends CharSequence, ROOT, CURRENT> StringAssertions<TARGET, ROOT, CURRENT> of(
      Function<@NotNull CURRENT, @Nullable String> extract,
      Rules<ROOT> rules,
      NullSpec<ROOT, CURRENT> parent) {
    return null;
  }
}
