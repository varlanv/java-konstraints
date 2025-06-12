package com.varlanv.konstraints;

import java.util.List;

final class EmptyToMutableViolations implements Violations {

  static final EmptyToMutableViolations INSTANCE = new EmptyToMutableViolations();

  @Override
  public List<Violation> list() {
    return List.of();
  }

  @Override
  public Violations add(Violation violation) {
    return new MutableTrustedViolations().add(violation);
  }

  @Override
  public boolean isEmpty() {
    return true;
  }
}
