package com.varlanv.konstraints;

import java.util.List;

abstract class Rules<SUBJECT> {

    private static final Rules<?> empty = new Rules<>() {
        @Override
        Rules<Object> add(Rule<Object> rule) {
            return create(List.of(rule));
        }

        @Override
        Rules<Object> merge(Rules<Object> other) {
            return other;
        }

        @Override
        Violations apply(Object t) {
            return Violations.create();
        }

        @Override
        List<Rule<Object>> list() {
            return List.of();
        }
    };

    abstract Rules<SUBJECT> add(Rule<SUBJECT> rule);

    abstract Rules<SUBJECT> merge(Rules<SUBJECT> other);

    abstract Violations apply(SUBJECT t);

    abstract List<Rule<SUBJECT>> list();

    static <SUBJECT> Rules<SUBJECT> empty() {
        @SuppressWarnings("unchecked")
        var instance = (Rules<SUBJECT>) empty;
        return instance;
    }

    static <SUBJECT> Rules<SUBJECT> create(List<Rule<SUBJECT>> rules) {
        return new Rules<>() {

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
                return rules;
            }
        };
    }
}
