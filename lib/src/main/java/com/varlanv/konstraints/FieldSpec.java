package com.varlanv.konstraints;

import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public final class FieldSpec<SUBJECT> {

    final AssertionsSpec<SUBJECT> parent;
    final String fieldName;

    public FieldSpec(AssertionsSpec<SUBJECT> parent, String fieldName) {
        this.parent = parent;
        this.fieldName = fieldName;
    }

    public AssertionsSpec<SUBJECT> assertNotNull(Function<SUBJECT, @Nullable Object> mapper) {
        return parent.withRule((subject, violations) -> {
            var val = mapper.apply(subject);
            if (val == null) {
                violations.add(Violation.of("is null"));
            }
            return violations;
        });
    }

    public AssertionsSpec<SUBJECT> assertNull(Function<SUBJECT, @Nullable Object> mapper) {
        return parent.withRule((subject, violations) -> {
            var val = mapper.apply(subject);
            if (val == null) {
                violations.add(Violation.of("is not null"));
            }
            return violations;
        });
    }

    public NullSpec<SUBJECT> nonNull() {
        return new NullSpec<>(this, false);
    }

    public NullSpec<SUBJECT> nullable() {
        return new NullSpec<>(this, true);
    }
}
