package com.varlanv.konstraints;

import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;

public interface Child<SELF, PARENT> {

    SELF value();

    PARENT parent();

    static <SELF, PARENT> Child<SELF, PARENT> of(SELF self, PARENT parent) {
        return new Child<>() {
            @Override
            public SELF value() {
                return self;
            }

            @Override
            public PARENT parent() {
                return parent;
            }
        };
    }

    static <SELF, PARENT> Child<SELF, PARENT> lazyParent(SELF self, Supplier<@NonNull PARENT> parent) {
        return new Child<>() {
            @Override
            public SELF value() {
                return self;
            }

            @Override
            public PARENT parent() {
                return parent.get();
            }
        };
    }

    static <SELF, PARENT> Child<SELF, PARENT> lazy(Supplier<SELF> self, Supplier<@NonNull PARENT> parent) {
        return new Child<>() {
            @Override
            public SELF value() {
                return self.get();
            }

            @Override
            public PARENT parent() {
                return parent.get();
            }
        };
    }
}
