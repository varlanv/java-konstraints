package com.varlanv.konstraints;

import java.util.function.Predicate;
import org.jspecify.annotations.NonNull;

public final class RootNumberAssertions<TARGET extends Comparable<TARGET>, ROOT> {

    public RootNumberAssertions<TARGET, ROOT> check(String message, Predicate<@NonNull TARGET> action) {
        return null;
    }

    public RootNumberAssertions<TARGET, ROOT> gte(TARGET target) {
        return null;
    }

    public RootNumberAssertions<TARGET, ROOT> lte(TARGET target) {
        return null;
    }

    public RootNumberAssertions<TARGET, ROOT> inRange(TARGET minTarget, TARGET maxTarget) {
        return null;
    }

    AssertionsSpec<ROOT> parent() {
        return null;
    }

    Rules<ROOT> rules() {
        return null;
    }
}
