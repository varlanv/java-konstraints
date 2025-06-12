package com.varlanv.konstraints;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

abstract class BaseStringAssertions<TARGET extends CharSequence, ROOT, CURRENT> extends RulesSpec<ROOT> {

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> check(
            Function<@NotNull String, @NotNull Violation> violation, Predicate<@NotNull TARGET> action);

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> check(
            String message, Predicate<@NotNull TARGET> action);

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> assertEmpty();

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> notEmpty();

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> assertNotBlank();

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> len(
            @Range(from = 1, to = Integer.MAX_VALUE) Integer length);

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> assertMinLength(
            @Range(from = 1, to = Integer.MAX_VALUE) Integer minLength);

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> assertMaxLength(
            @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> assertLengthRange(
            @Range(from = 0, to = Integer.MAX_VALUE) Integer minLength,
            @Range(from = 1, to = Integer.MAX_VALUE) Integer maxLength);

    public abstract BaseStringAssertions<TARGET, ROOT, CURRENT> assertMatches(Pattern pattern);
}
