package com.varlanv.konstraints;

import java.util.ArrayList;
import java.util.List;

final class MutableTrustedViolations implements Violations {

  private final List<Violation> violations;

  MutableTrustedViolations() {
    this.violations = new ArrayList<>(5);
  }

  @Override
  public List<Violation> list() {
    return violations;
  }

  @Override
  public Violations add(Violation violation) {
    this.violations.add(violation);
    return this;
  }

  @Override
  public boolean isEmpty() {
    return violations.isEmpty();
  }
}
