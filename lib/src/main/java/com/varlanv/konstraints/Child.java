package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface Child<SELF, PARENT> {

  SELF value();

  PARENT parent();

  static <SELF, PARENT> Child<SELF, PARENT> of(SELF self, PARENT parent) {
    return new Child<>() {
      @Override
      public SELF value() {
        return self;
      }

      @Override
      public PARENT parent() {
        return parent;
      }
    };
  }

  static <SELF, PARENT> Child<SELF, PARENT> lazyParent(SELF self, Supplier<@NotNull PARENT> parent) {
    return new Child<>() {
      @Override
      public SELF value() {
        return self;
      }

      @Override
      public PARENT parent() {
        return parent.get();
      }
    };
  }

  static <SELF, PARENT> Child<SELF, PARENT> lazy(Supplier<SELF> self, Supplier<@NotNull PARENT> parent) {
    return new Child<>() {
      @Override
      public SELF value() {
        return self.get();
      }

      @Override
      public PARENT parent() {
        return parent.get();
      }
    };
  }
}
