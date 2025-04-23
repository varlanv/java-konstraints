package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface BaseAssertionsSpec<ROOT, CURRENT> extends RulesSpec<ROOT> {

  BaseFieldSpec<ROOT, CURRENT> field(String fieldName);

  BaseAssertionsSpec<ROOT, CURRENT> withRule(Rule<ROOT> rule);

  BaseAssertionsSpec<ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

  Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();
}
