package com.varlanv.konstraints;

import java.util.List;

final class TrustedRules<SUBJECT> implements Rules<SUBJECT> {

    private final List<Rule<SUBJECT>> rules;

    TrustedRules(List<Rule<SUBJECT>> rules) {
        this.rules = rules;
    }

    @Override
    public Rules<SUBJECT> add(Rule<SUBJECT> rule) {
        return Rules.create(Internals.newListWithItem(rules, rule));
    }

    @Override
    public Rules<SUBJECT> merge(Rules<SUBJECT> other) {
        var otherRules = other.list();
        if (otherRules.isEmpty()) {
            return this;
        }
        return Rules.create(Internals.mergeLists(rules, otherRules));
    }

    @Override
    public Violations apply(SUBJECT t) {
        Violations violations = EmptyToMutableViolations.INSTANCE;
        for (var rule : rules) {
            violations = rule.apply(t, violations);
        }
        return ImmutableTrustedViolations.of(violations.list());
    }

    @Override
    public List<Rule<SUBJECT>> list() {
        return List.of();
    }

    @Override
    public boolean isEmpty() {
        return rules.isEmpty();
    }
}
