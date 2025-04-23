package com.varlanv.konstraints;

import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Rules<SUBJECT> {

  Rules<SUBJECT> add(Rule<SUBJECT> rule);

  Rules<SUBJECT> merge(Rules<SUBJECT> other);

  @Unmodifiable
  List<Violation> apply(SUBJECT t);

  List<Rule<SUBJECT>> list();

  static <SUBJECT> Rules<SUBJECT> create() {
    return create(List.of());
  }

  static <SUBJECT> Rules<SUBJECT> create(List<Rule<SUBJECT>> incomeRules) {
    var rules = List.copyOf(incomeRules);
    return new Rules<>() {

      @Override
      public Rules<SUBJECT> add(Rule<SUBJECT> rule) {
        return Rules.create(Internals.addToList(rules, rule));
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
      public List<Violation> apply(SUBJECT t) {
        List<Violation> violations = null;
        for (var rule : rules) {
          var maybeViolation = rule.apply(t);
          if (maybeViolation.isPresent()) {
            if (violations == null) {
              violations = new ArrayList<>(5);
            }
            violations.add(maybeViolation.get());
          }
        }
        return violations == null ? List.of() : Collections.unmodifiableList(violations);
      }

      @Override
      public List<Rule<SUBJECT>> list() {
        return rules;
      }
    };
  }
}
