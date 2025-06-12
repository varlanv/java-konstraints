package com.varlanv.konstraints;

public interface IndexedChild<SELF, PARENT> extends Child<SELF, PARENT> {

    int index();

    static <SELF, PARENT> IndexedChild<SELF, PARENT> of(SELF self, PARENT parent, int index) {
        return new IndexedChild<>() {
            @Override
            public SELF value() {
                return self;
            }

            @Override
            public PARENT parent() {
                return parent;
            }

            @Override
            public int index() {
                return index;
            }
        };
    }
}
