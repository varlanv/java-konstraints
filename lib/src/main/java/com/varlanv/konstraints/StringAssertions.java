package com.varlanv.konstraints;

import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class StringAssertions<TARGET extends CharSequence, SUBJECT> {

    private final NullSpec<SUBJECT> parent;
    final Rules<TARGET> rules;

    StringAssertions(
        NullSpec<SUBJECT> parent, Rules<TARGET> rules) {
        this.parent = parent;
        this.rules = rules;
    }

    public StringAssertions<TARGET, SUBJECT> check(String message, Predicate<@NonNull TARGET> action) {
        return null;
    }

    public StringAssertions<TARGET, SUBJECT> empty() {
        return null;
    }

    public StringAssertions<TARGET, SUBJECT> notEmpty() {
        return null;
    }

    public StringAssertions<TARGET, SUBJECT> notBlank() {
        return null;
    }

    public StringAssertions<TARGET, SUBJECT> length(@Range(from = 1, to = Integer.MAX_VALUE) int length) {
        return null;
    }

    public StringAssertions<TARGET, SUBJECT> minLength(@Range(from = 1, to = Integer.MAX_VALUE) int minLength) {
        return null;
    }

    public StringAssertions<TARGET, SUBJECT> maxLength(@Range(from = 1, to = Integer.MAX_VALUE) int maxLength) {
        return null;
    }

    public StringAssertions<TARGET, SUBJECT> lengthRange(
        @Range(from = 0, to = Integer.MAX_VALUE) int minLength,
        @Range(from = 1, to = Integer.MAX_VALUE) int maxLength) {
        return null;
    }

    public StringAssertions<TARGET, SUBJECT> matches(Pattern pattern) {
        return new StringAssertions<>(parent, rules.add(((target, violations) -> {
            if (!pattern.matcher(target.toString()).matches()) {
                return violations.add(Violation.of("not match"));
            }
            return violations;
        })));
    }
}
