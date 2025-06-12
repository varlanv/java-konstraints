package com.varlanv.konstraints;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public final class CustomAssertions<TARGET, ROOT, CURRENT> extends BaseCustomAssertions<TARGET, ROOT, CURRENT> {

    @Override
    CustomAssertions<TARGET, ROOT, CURRENT> assertTrue(Function<@NotNull TARGET, @NotNull Boolean> action) {
        return null;
    }

    @Override
    RootNullSpec<ROOT, CURRENT> parent() {
        return null;
    }

    @Override
    Rules<ROOT> rules() {
        return null;
    }
}
