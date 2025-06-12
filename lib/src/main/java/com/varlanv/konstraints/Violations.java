package com.varlanv.konstraints;

import java.util.List;

public interface Violations {

    List<Violation> list();

    Violations add(Violation violation);

    boolean isEmpty();

    static Violations of(List<Violation> violations) {
        return ImmutableTrustedViolations.of(List.copyOf(violations));
    }

    static Violations of(Violation... violations) {
        return violations.length == 0 ? create() : ImmutableTrustedViolations.of(violations.clone());
    }

    static Violations create() {
        return EmptyViolations.INSTANCE;
    }
}
