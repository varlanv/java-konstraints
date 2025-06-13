package com.varlanv.konstraints;

public final class AssertionsSpec<SUBJECT> {

    private final Rules<SUBJECT> rules;

    AssertionsSpec(Rules<SUBJECT> rules) {
        this.rules = rules;
    }

    public FieldSpec<SUBJECT> field(String fieldName) {
        return new FieldSpec<>(this, fieldName);
    }

    AssertionsSpec<SUBJECT> withRule(Rule<SUBJECT> rule) {
        return new AssertionsSpec<>(this.rules.add(rule));
    }

    AssertionsSpec<SUBJECT> mergeRules(Rules<SUBJECT> rules) {
        return new AssertionsSpec<>(this.rules.merge(rules));
    }

    Rules<SUBJECT> rules() {
        return null;
    }
}
