package com.varlanv.konstraints;

import java.util.List;

final class EmptyViolations implements Violations {

  static final EmptyViolations INSTANCE = new EmptyViolations();

  @Override
  public List<Violation> list() {
    return List.of();
  }

  @Override
  public Violations add(Violation violation) {
    return ImmutableTrustedViolations.of(violation);
  }

  @Override
  public boolean isEmpty() {
    return true;
  }
}
