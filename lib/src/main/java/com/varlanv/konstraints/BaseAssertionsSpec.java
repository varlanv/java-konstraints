package com.varlanv.konstraints;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class BaseAssertionsSpec<ROOT, CURRENT> extends RulesSpec<ROOT> {

    public abstract BaseFieldSpec<ROOT, CURRENT> field(String fieldName);

    abstract BaseAssertionsSpec<ROOT, CURRENT> withRule(Rule<ROOT> rule);

    abstract BaseAssertionsSpec<ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

    abstract Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();
}
