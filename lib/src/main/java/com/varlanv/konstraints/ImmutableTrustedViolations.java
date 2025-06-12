package com.varlanv.konstraints;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class ImmutableTrustedViolations implements Violations {

  List<Violation> violations;

  private ImmutableTrustedViolations(List<Violation> violations) {
    this.violations = violations;
  }

  static Violations of(Violation violation) {
    return new ImmutableTrustedViolations(Collections.singletonList(violation));
  }

  static Violations of(List<Violation> violations) {
    return violations.isEmpty() ? Violations.create() :
        new ImmutableTrustedViolations(Collections.unmodifiableList(violations));
  }

  static Violations of(Violation... violations) {
    return violations.length == 0 ? Violations.create() :
        new ImmutableTrustedViolations(Arrays.asList(violations));
  }

  @Override
  public List<Violation> list() {
    return violations;
  }

  @Override
  public Violations add(Violation violation) {
    return new ImmutableTrustedViolations(Internals.newListWithItem(violations, violation));
  }

  @Override
  public boolean isEmpty() {
    return violations.isEmpty();
  }
}
