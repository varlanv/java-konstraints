package com.varlanv.konstraints;

import java.util.List;

interface Rules<SUBJECT> {

    Rules<SUBJECT> add(Rule<SUBJECT> rule);

    Rules<SUBJECT> merge(Rules<SUBJECT> other);

    Violations apply(SUBJECT t);

    List<Rule<SUBJECT>> list();

    boolean isEmpty();

    static <SUBJECT> Rules<SUBJECT> empty() {
        @SuppressWarnings("unchecked")
        var instance = (Rules<SUBJECT>) EmptyRules.INSTANCE;
        return instance;
    }

    static <SUBJECT> Rules<SUBJECT> create(List<Rule<SUBJECT>> rules) {
        return new TrustedRules<>(List.copyOf(rules));
    }
}
