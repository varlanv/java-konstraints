package com.varlanv.konstraints;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class NullSpec<SUBJECT> {

    final FieldSpec<SUBJECT> parent;
    final boolean nullable;

    public NullSpec(FieldSpec<SUBJECT> parent, boolean nullable) {
        this.parent = parent;
        this.nullable = nullable;
    }

    public <TARGET extends CharSequence> AssertionsSpec<SUBJECT> string(
        Function<@NonNull SUBJECT, @Nullable TARGET> mapper,
        UnaryOperator<@NonNull StringAssertions<TARGET, SUBJECT>> action) {
        var stringAssertions = action.apply(new StringAssertions<>(this, Rules.empty()));
        if (stringAssertions.rules.list() == Rules.empty()) {
            return parent.parent;
        }
        return parent.parent.withRule(((subject, violations) -> {
            TARGET target = mapper.apply(subject);
            if (target == null) {
                return violations.add(Violation.of(parent.fieldName, "is null"));
            } else {
                var result = violations;
                for (Rule<TARGET> targetRule : stringAssertions.rules.list()) {
                    for (var violation : targetRule.apply(target, violations).list()) {
                        result = result.add(violation);
                    }
                }
                return result;
            }
        }));
    }

    public <TARGET extends Number & Comparable<TARGET>> AssertionsSpec<SUBJECT> number(
        Function<@NonNull SUBJECT, @Nullable TARGET> extract,
        Function<@NonNull RootNumberAssertions<TARGET, SUBJECT>, @NonNull RootNumberAssertions<TARGET, SUBJECT>>
            action) {
        return null;
    }

    public <TARGET> AssertionsSpec<SUBJECT> nested(
        Function<@NonNull SUBJECT, @Nullable TARGET> extract, AssertionsOperator<TARGET> action) {
        AssertionsSpec<TARGET> apply = action.apply(new AssertionsSpec<>(Rules.empty()));
        return null;
    }
}
