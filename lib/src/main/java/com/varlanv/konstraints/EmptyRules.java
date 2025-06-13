package com.varlanv.konstraints;

import java.util.List;

final class EmptyRules<SUBJECT> implements Rules<SUBJECT> {

    static final EmptyRules<Object> INSTANCE = new EmptyRules<>();

    @Override
    public Rules<SUBJECT> add(Rule<SUBJECT> rule) {
        return Rules.create(List.of(rule));
    }

    @Override
    public Rules<SUBJECT> merge(Rules<SUBJECT> other) {
        return other;
    }

    @Override
    public Violations apply(SUBJECT t) {
        return Violations.create();
    }

    @Override
    public List<Rule<SUBJECT>> list() {
        return List.of();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
