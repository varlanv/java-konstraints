package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

@NotNullByDefault
public interface StaticValid<SUBJECT> {

  <NEXT_SUBJECT> StaticValid<NEXT_SUBJECT> map(Function<SUBJECT, NEXT_SUBJECT> mapper);

  <NEXT_SUBJECT> StaticValid<NEXT_SUBJECT> flatMap(Function<SUBJECT, StaticValid<NEXT_SUBJECT>> mapper);

  SUBJECT orElseThrow(Supplier<? extends Throwable> exceptionSupplier);

  SUBJECT orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn);

  Optional<SUBJECT> optional();

  boolean isValid();

  boolean isNotValid();

  @Unmodifiable
  List<Violation> violations();

  static <SUBJECT> StaticValid<SUBJECT> valid(SUBJECT value) {
    Objects.requireNonNull(value);
    return valid(() -> value);
  }

  static <SUBJECT> StaticValid<SUBJECT> valid(Supplier<@NotNull SUBJECT> supplier) {
    Objects.requireNonNull(supplier);
    return new StaticValid<>() {

      @Override
      public <R> StaticValid<R> map(Function<SUBJECT, R> mapper) {
        return valid(() -> mapper.apply(supplier.get()));
      }

      @Override
      public <R> StaticValid<R> flatMap(Function<SUBJECT, StaticValid<R>> mapper) {
        return mapper.apply(supplier.get());
      }

      @Override
      public SUBJECT orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
        return supplier.get();
      }

      @Override
      public SUBJECT orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn) {
        return supplier.get();
      }

      @Override
      public Optional<SUBJECT> optional() {
        return Optional.of(supplier.get());
      }

      @Override
      public boolean isValid() {
        return true;
      }

      @Override
      public boolean isNotValid() {
        return false;
      }

      @Override
      public List<Violation> violations() {
        return List.of();
      }
    };
  }

  static <SUBJECT> StaticValid<SUBJECT> invalid(List<Violation> violations) {
    if (violations.isEmpty()) {
      throw new IllegalArgumentException("Violations must not be empty");
    }
    var violationsCopy = List.copyOf(violations);
    return new StaticValid<>() {

      @Override
      public <NEW_SUBJECT> StaticValid<NEW_SUBJECT> map(Function<SUBJECT, NEW_SUBJECT> mapper) {
        return self();
      }

      @Override
      public <NEW_SUBJECT> StaticValid<NEW_SUBJECT> flatMap(Function<SUBJECT, StaticValid<NEW_SUBJECT>> mapper) {
        return self();
      }

      @Override
      public SUBJECT orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
        throw StaticValid.hide(exceptionSupplier.get());
      }

      @Override
      public SUBJECT orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn) {
        throw StaticValid.hide(exceptionFn.apply(violationsCopy));
      }

      @Override
      public Optional<SUBJECT> optional() {
        return Optional.empty();
      }

      @Override
      public boolean isValid() {
        return false;
      }

      @Override
      public boolean isNotValid() {
        return true;
      }

      @Override
      public List<Violation> violations() {
        return violationsCopy;
      }

      private <MIRROR> StaticValid<MIRROR> self() {
        @SuppressWarnings("unchecked")
        var self = (StaticValid<MIRROR>) this;
        return self;
      }
    };
  }

  static <SUBJECT> ValidationSpec<SUBJECT> validationSpec(
      Function<AssertionsSpec<SUBJECT, SUBJECT>, AssertionsSpec<SUBJECT, SUBJECT>> specAction) {
    return ValidationSpec.fromRules(
        specAction.apply(
            AssertionsSpec.from(
                Rules.create(),
                Function.identity()
            )
        ).rules()
    );
  }

  interface Rules<SUBJECT> {

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
          return Rules.create(addToList(rules, rule));
        }

        @Override
        public Rules<SUBJECT> merge(Rules<SUBJECT> other) {
          var otherRules = other.list();
          if (otherRules.isEmpty()) {
            return this;
          }
          return Rules.create(mergeLists(rules, otherRules));
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

  interface ValidationSpec<SUBJECT> {

    Function<SUBJECT, StaticValid<SUBJECT>> toValidationFunction();

    UnaryOperator<SUBJECT> toValidationFunction(
        Function<List<Violation>, ? extends Throwable> onException);

    UnaryOperator<SUBJECT> toValidationFunction(Supplier<? extends Throwable> onException);

    StaticValid<SUBJECT> validate(SUBJECT t);

    static <T> ValidationSpec<T> fromRules(Rules<T> rules) {
      Objects.requireNonNull(rules);
      return new ValidationSpec<>() {

        @Override
        public Function<T, StaticValid<T>> toValidationFunction() {
          return this::validate;
        }

        @Override
        public UnaryOperator<T> toValidationFunction(
            Function<List<Violation>, ? extends Throwable> onException) {
          Objects.requireNonNull(onException);
          return t -> validate(t).orElseThrow(onException);
        }

        @Override
        public UnaryOperator<T> toValidationFunction(Supplier<? extends Throwable> onException) {
          Objects.requireNonNull(onException);
          return t -> validate(t).orElseThrow(onException);
        }

        @Override
        public StaticValid<T> validate(T t) {
          Objects.requireNonNull(t);
          var violations = rules.apply(t);
          if (violations.isEmpty()) {
            return StaticValid.valid(t);
          } else {
            return StaticValid.invalid(violations);
          }
        }
      };
    }
  }

  interface Rule<SUBJECT> extends Function<@NotNull SUBJECT, @NotNull Optional<Violation>> {
  }

  interface AssertionsSpec<ROOT, CURRENT> {

    FieldSpec<ROOT, CURRENT> field(String fieldName);

    AssertionsSpec<ROOT, CURRENT> withRule(Rule<ROOT> rule);

    AssertionsSpec<ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

    Rules<ROOT> rules();

    Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();

    static <ROOT, CURRENT> AssertionsSpec<ROOT, CURRENT> from(
        Rules<ROOT> rules, Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn) {
      return new AssertionsSpec<>() {

        @Override
        public FieldSpec<ROOT, CURRENT> field(String fieldName) {
          return FieldSpec.of(fieldName, this);
        }

        @Override
        public AssertionsSpec<ROOT, CURRENT> withRule(Rule<ROOT> rule) {
          rules.add(rule);
          return this;
        }

        @Override
        public AssertionsSpec<ROOT, CURRENT> mergeRules(Rules<ROOT> rules) {
          return AssertionsSpec.from(
              rules.merge(rules),
              currentNestFn
          );
        }

        @Override
        public Rules<ROOT> rules() {
          return rules;
        }

        @Override
        public Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn() {
          return currentNestFn;
        }
      };
    }
  }

  interface NestedAssertionsSpec<PARENT, ROOT, CURRENT> {

    NestedFieldSpec<PARENT, ROOT, CURRENT> field(String fieldName);

    NestedAssertionsSpec<PARENT, ROOT, CURRENT> withRule(Rule<ROOT> rule);

    NestedAssertionsSpec<PARENT, ROOT, CURRENT> mergeRules(Rules<ROOT> rules);

    Rules<ROOT> rules();

    Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn();

    PARENT parent();

    static <PARENT, ROOT, CURRENT> NestedAssertionsSpec<PARENT, ROOT, CURRENT> of(
        Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn,
        Rules<ROOT> rules,
        PARENT parent) {
      return new NestedAssertionsSpec<>() {
        @Override
        public NestedFieldSpec<PARENT, ROOT, CURRENT> field(String fieldName) {
          return null;
        }

        @Override
        public NestedAssertionsSpec<PARENT, ROOT, CURRENT> withRule(Rule<ROOT> rule) {
          return null;
        }

        @Override
        public NestedAssertionsSpec<PARENT, ROOT, CURRENT> mergeRules(Rules<ROOT> rules) {
          return null;
        }

        @Override
        public Rules<ROOT> rules() {
          return null;
        }

        @Override
        public Function<@NotNull ROOT, @Nullable CURRENT> currentNestFn() {
          return null;
        }

        @Override
        public PARENT parent() {
          return parent;
        }
      };
    }
  }

  interface NestedFieldSpec<PARENT, ROOT, CURRENT> {

    <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

    <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

    NestedNullSpec<PARENT, ROOT, CURRENT> nonNull();

    NestedNullSpec<PARENT, ROOT, CURRENT> nullable();

    String fieldName();

    NestedAssertionsSpec<PARENT, ROOT, CURRENT> parent();
  }

  interface FieldSpec<ROOT, CURRENT> {

    <TARGET> AssertionsSpec<ROOT, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

    <TARGET> AssertionsSpec<ROOT, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

    NullSpec<ROOT, CURRENT> nonNull();

    NullSpec<ROOT, CURRENT> nullable();

    String fieldName();

    AssertionsSpec<ROOT, CURRENT> parent();

    static <ROOT, CURRENT> FieldSpec<ROOT, CURRENT> of(
        String fieldName, AssertionsSpec<ROOT, CURRENT> parent) {
      return new FieldSpec<>() {

        @Override
        public <TARGET> AssertionsSpec<ROOT, CURRENT> assertNotNull(
            Function<CURRENT, TARGET> extract) {
          return parent().withRule(
              t -> {
                if (parent().currentNestFn().apply(t) == null) {
                  return Optional.of(Violation.of(fieldName(), "expected to be non-null"));
                } else {
                  return Optional.empty();
                }
              });
        }

        @Override
        public <TARGET> AssertionsSpec<ROOT, CURRENT> assertNull(
            Function<CURRENT, TARGET> extract) {
          return parent().withRule(
              t -> {
                if (parent().currentNestFn().apply(t) != null) {
                  return Optional.of(Violation.of(fieldName(), "expected to be null"));
                } else {
                  return Optional.empty();
                }
              });
        }

        @Override
        public NullSpec<ROOT, CURRENT> nullable() {
          return NullSpec.of(true, this);
        }

        @Override
        public NullSpec<ROOT, CURRENT> nonNull() {
          return NullSpec.of(false, this);
        }

        @Override
        public String fieldName() {
          return fieldName;
        }

        @Override
        public AssertionsSpec<ROOT, CURRENT> parent() {
          return parent;
        }
      };
    }
  }

  interface NestedNullSpec<PARENT, ROOT, CURRENT> {

    <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> custom(
        Function<@NotNull CURRENT, @Nullable TARGET> extract,
        Function<@NotNull NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT>,
            @NotNull NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT>> action);

    NestedAssertionsSpec<PARENT, ROOT, CURRENT> string(
        Function<@NotNull CURRENT, @Nullable String> extract,
        Function<@NotNull StringAssertions<ROOT, CURRENT>, @NotNull StringAssertions<ROOT, CURRENT>> action);

    <TARGET extends Number & Comparable<TARGET>> NestedAssertionsSpec<PARENT, ROOT, CURRENT> number(
        Function<@NotNull CURRENT, @Nullable TARGET> extract,
        Function<@NotNull NumberAssertions<TARGET, ROOT, CURRENT>,
            @NotNull NumberAssertions<TARGET, ROOT, CURRENT>> action);

    <TARGET> NestedAssertionsSpec<PARENT, ROOT, CURRENT> nested(
        Function<@NotNull CURRENT, @Nullable TARGET> extract,
        Function<@NotNull NestedAssertionsSpec<Child<TARGET, PARENT>, CURRENT, TARGET>,
            @NotNull NestedAssertionsSpec<Child<TARGET, PARENT>, CURRENT, TARGET>> action);

    NestedFieldSpec<ROOT, PARENT, CURRENT> parent();

    IterableSpec<ROOT, CURRENT> iterable();

    Boolean allowNull();
  }

  interface NullSpec<ROOT, CURRENT> {

    <TARGET> AssertionsSpec<ROOT, CURRENT> custom(Function<@NotNull CURRENT, @Nullable TARGET> extract,
                                                  Function<@NotNull CustomAssertions<TARGET, ROOT, CURRENT>,
                                                      @NotNull CustomAssertions<TARGET, ROOT, CURRENT>> action);

    AssertionsSpec<ROOT, CURRENT> string(
        Function<@NotNull CURRENT, @Nullable String> extract,
        Function<@NotNull StringAssertions<ROOT, CURRENT>, @NotNull StringAssertions<ROOT, CURRENT>> action);

    <TARGET extends Number & Comparable<TARGET>> AssertionsSpec<ROOT, CURRENT> number(
        Function<@NotNull CURRENT, @Nullable TARGET> extract,
        Function<@NotNull NumberAssertions<TARGET, ROOT, CURRENT>,
            @NotNull NumberAssertions<TARGET, ROOT, CURRENT>> action);

    <TARGET> AssertionsSpec<ROOT, CURRENT> nested(
        Function<@NotNull CURRENT, @Nullable TARGET> extract,
        Function<@NotNull NestedAssertionsSpec<Child<TARGET, CURRENT>, CURRENT, TARGET>,
            @NotNull NestedAssertionsSpec<Child<TARGET, CURRENT>, CURRENT, TARGET>> action);

    FieldSpec<ROOT, CURRENT> parent();

    IterableSpec<ROOT, CURRENT> iterable();

    Boolean allowNull();

    static <ROOT, CURRENT> NullSpec<ROOT, CURRENT> of(Boolean allowNull, FieldSpec<ROOT, CURRENT> parent) {
      return new NullSpec<>() {

        @Override
        public <TARGET> AssertionsSpec<ROOT, CURRENT> custom(
            Function<@NotNull CURRENT, @Nullable TARGET> extract,
            Function<@NotNull CustomAssertions<TARGET, ROOT, CURRENT>,
                @NotNull CustomAssertions<TARGET, ROOT, CURRENT>> action) {
          return parent().parent().mergeRules(
              action.apply(
                  CustomAssertions.of(
                      extract,
                      Rules.create(),
                      this
                  )
              ).rules()
          );
        }

        @Override
        public AssertionsSpec<ROOT, CURRENT> string(
            Function<@NotNull CURRENT, @Nullable String> extract,
            Function<@NotNull StringAssertions<ROOT, CURRENT>, StringAssertions<ROOT, CURRENT>> action) {
          return parent().parent().mergeRules(
              action.apply(
                  StringAssertions.of(
                      extract,
                      Rules.create(),
                      this
                  )
              ).rules()
          );
        }

        @Override
        public <TARGET extends Number & Comparable<TARGET>> AssertionsSpec<ROOT, CURRENT> number(
            Function<@NotNull CURRENT, @Nullable TARGET> extract,
            Function<@NotNull NumberAssertions<TARGET, ROOT, CURRENT>, @NotNull NumberAssertions<TARGET, ROOT, CURRENT>> action) {
          return parent().parent().mergeRules(
              action.apply(
                  NumberAssertions.of(
                      extract,
                      Rules.create(),
                      this
                  )
              ).rules()
          );
        }

        @Override
        public <TARGET> AssertionsSpec<ROOT, CURRENT> nested(
            Function<@NotNull CURRENT, @Nullable TARGET> extract,
            Function<@NotNull NestedAssertionsSpec<Child<TARGET, CURRENT>, CURRENT, TARGET>,
                @NotNull NestedAssertionsSpec<Child<TARGET, CURRENT>, CURRENT, TARGET>> action) {
          var parent = parent().parent();
          var rules = NestedAssertionsSpec.of(
              extract,
              Rules.create(),
              parent
          ).rules();
          return parent.mergeRules(
              Rules.create(
                  rules.list().stream()
                      .map(rule -> {
                        return (Rule<ROOT>) t -> {
                          CURRENT current = parent().parent().currentNestFn().apply(t);
                          return rule.apply(current);
                        };
                      })
                      .toList()
              )
          );
        }

        @Override
        public FieldSpec<ROOT, CURRENT> parent() {
          return parent;
        }

        @Override
        public IterableSpec<ROOT, CURRENT> iterable() {
          return IterableSpec.of(this);
        }

        @Override
        public Boolean allowNull() {
          return allowNull;
        }
      };
    }
  }

  interface CustomAssertions<TARGET, ROOT, CURRENT> {

    CustomAssertions<TARGET, ROOT, CURRENT> assertTrue(
        Function<@NotNull CURRENT, @Nullable TARGET> extract, Function<@NotNull TARGET, @NotNull Boolean> action);

    Rules<ROOT> rules();

    NullSpec<ROOT, CURRENT> parent();

    static <TARGET, ROOT, CURRENT> CustomAssertions<TARGET, ROOT, CURRENT> of(Function<@NotNull CURRENT, @Nullable TARGET> extract,
                                                                              Rules<ROOT> rules,
                                                                              NullSpec<ROOT, CURRENT> parent) {
      return new CustomAssertions<>() {

        @Override
        public CustomAssertions<TARGET, ROOT, CURRENT> assertTrue(
            Function<@NotNull CURRENT, @Nullable TARGET> extract, Function<@NotNull TARGET, @NotNull Boolean> action) {
          return CustomAssertions.of(
              extract,
              rules.add(t -> {
                CURRENT current = parent().parent().parent().currentNestFn().apply(t);
                TARGET target = extract.apply(current);
                if (action.apply(target)) {
                  return Optional.of(Violation.of("Fail"));
                } else {
                  return Optional.empty();
                }
              }),
              parent
          );
        }

        @Override
        public Rules<ROOT> rules() {
          return rules;
        }

        @Override
        public NullSpec<ROOT, CURRENT> parent() {
          return parent;
        }
      };
    }
  }

  interface NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> {

    NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
        Function<@NotNull TARGET, @NotNull Boolean> action);

    NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
        BiFunction<@NotNull TARGET, @NotNull PARENT, @NotNull Boolean> action);

    Rules<ROOT> rules();

    NullSpec<ROOT, CURRENT> parent();

    static <TARGET, PARENT, ROOT, CURRENT> NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> of(
        Function<@NotNull CURRENT, @Nullable TARGET> extract,
        Rules<ROOT> rules,
        Supplier<PARENT> parentSupplier,
        NullSpec<ROOT, CURRENT> parent) {
      return new NestedCustomAssertions<>() {

        @Override
        public NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(Function<@NotNull TARGET, @NotNull Boolean> action) {
          return assertTrue((a, b) -> action.apply(a));
        }

        @Override
        public NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(BiFunction<@NotNull TARGET, @NotNull PARENT, @NotNull Boolean> action) {
          return NestedCustomAssertions.of(
              extract,
              rules.add(t -> {
                CURRENT current = parent().parent().parent().currentNestFn().apply(t);
                TARGET target = extract.apply(current);
                if (action.apply(target, parentSupplier.get())) {
                  return Optional.of(Violation.of("Fail"));
                } else {
                  return Optional.empty();
                }
              }),
              parentSupplier,
              parent
          );
        }

        @Override
        public Rules<ROOT> rules() {
          return rules;
        }

        @Override
        public NullSpec<ROOT, CURRENT> parent() {
          return parent;
        }
      };
    }
  }

  interface IterableSpec<ROOT, CURRENT> {

    <ITEM extends CharSequence, ITEMS extends Iterable<ITEM>>
    IterableAssertions<StringAssertions<ROOT, CURRENT>, ITEM, ITEMS, ROOT, CURRENT> strings(
        Function<@NotNull CURRENT, @Nullable ITEMS> extract);

    <ITEM extends Comparable<ITEM>, ITEMS extends Iterable<ITEM>>
    IterableAssertions<NumberAssertions<ITEM, ROOT, CURRENT>, ITEM, ITEMS, ROOT, CURRENT> numbers(
        Function<@NotNull CURRENT, @Nullable ITEMS> extract
    );

    <ITEM, ITEMS extends Iterable<ITEM>>
    IterableAssertions<AssertionsSpec<ROOT, ITEM>, ITEM, ITEMS, ROOT, CURRENT> nested(
        Function<@NotNull CURRENT, @Nullable ITEMS> extract);

    NullSpec<ROOT, CURRENT> parent();

    static <ROOT, CURRENT> IterableSpec<ROOT, CURRENT> of(NullSpec<ROOT, CURRENT> nullSpec) {
      return new IterableSpec<>() {
        @Override
        public <ITEM extends CharSequence, ITEMS extends Iterable<ITEM>>
        IterableAssertions<StringAssertions<ROOT, CURRENT>, ITEM, ITEMS, ROOT, CURRENT> strings(
            Function<@NotNull CURRENT, @Nullable ITEMS> extract) {
          return IterableAssertions.of(extract, this);
        }

        @Override
        public <ITEM extends Comparable<ITEM>, ITEMS extends Iterable<ITEM>>
        IterableAssertions<NumberAssertions<ITEM, ROOT, CURRENT>, ITEM, ITEMS, ROOT, CURRENT> numbers(
            Function<@NotNull CURRENT, @Nullable ITEMS> extract) {
          return IterableAssertions.of(extract, this);
        }

        @Override
        public <ITEM, ITEMS extends Iterable<ITEM>>
        IterableAssertions<AssertionsSpec<ROOT, ITEM>, ITEM, ITEMS, ROOT, CURRENT> nested(
            Function<@NotNull CURRENT, @Nullable ITEMS> extract) {
          return IterableAssertions.of(extract, this);
        }

        @Override
        public NullSpec<ROOT, CURRENT> parent() {
          return nullSpec;
        }
      };
    }
  }

  interface IterableAssertions<ASSERTIONS, ITEM, ITEMS extends Iterable<ITEM>, ROOT, CURRENT> {

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertSize(Integer size);

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertMinSize(Integer minSize);

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertMaxSize(Integer maxSize);

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertSizeRange(Integer minSize, Integer maxSize);

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertNotEmpty();

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertEmpty();

    AssertionsSpec<ROOT, CURRENT> eachItem(Function<ASSERTIONS, ?> action);

    static <
        ASSERTIONS,
        ITEM,
        ITEMS extends Iterable<ITEM>,
        ROOT,
        CURRENT> IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> of(
        Function<@NotNull CURRENT, @Nullable ITEMS> extract, IterableSpec<ROOT, CURRENT> parent) {
      return new IterableAssertions<>() {
        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertSize(Integer size) {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertMinSize(Integer minSize) {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertMaxSize(Integer maxSize) {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertSizeRange(Integer minSize, Integer maxSize) {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertNotEmpty() {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, ROOT, CURRENT> assertEmpty() {
          return this;
        }

        @Override
        public AssertionsSpec<ROOT, CURRENT> eachItem(Function<ASSERTIONS, ?> action) {
          return parent.parent().parent().parent();
        }
      };
    }
  }

  interface NumberAssertions<TARGET extends Comparable<TARGET>, ROOT, CURRENT> {

    Rules<ROOT> rules();

    NumberAssertions<TARGET, ROOT, CURRENT> isGte(TARGET target);

    NumberAssertions<TARGET, ROOT, CURRENT> isLte(TARGET target);

    NumberAssertions<TARGET, ROOT, CURRENT> isInRange(TARGET minTarget, TARGET maxTarget);

    static <TARGET extends Number & Comparable<TARGET>, ROOT, CURRENT> NumberAssertions<TARGET, ROOT, CURRENT> of(
        Function<@NotNull CURRENT, @Nullable TARGET> extract,
        Rules<ROOT> rules,
        NullSpec<ROOT, CURRENT> parent) {
      return new NumberAssertions<>() {

        @Override
        public Rules<ROOT> rules() {
          return rules;
        }

        @Override
        public NumberAssertions<TARGET, ROOT, CURRENT> isGte(TARGET target) {
          return this;
        }

        @Override
        public NumberAssertions<TARGET, ROOT, CURRENT> isLte(TARGET target) {
          return this;
        }

        @Override
        public NumberAssertions<TARGET, ROOT, CURRENT> isInRange(
            TARGET minTarget, TARGET maxTarget) {
          return this;
        }
      };
    }
  }

  interface StringAssertions<ROOT, CURRENT> {

    Rules<ROOT> rules();

    StringAssertions<ROOT, CURRENT> isEmpty();

    StringAssertions<ROOT, CURRENT> isNotBlank();

    StringAssertions<ROOT, CURRENT> assertLength(Integer length);

    StringAssertions<ROOT, CURRENT> hasMinLength(Integer minLength);

    StringAssertions<ROOT, CURRENT> hasMaxLength(Integer maxLength);

    StringAssertions<ROOT, CURRENT> hasLengthRange(Integer minLength, Integer maxLength);

    StringAssertions<ROOT, CURRENT> matches(Pattern pattern);

    static <ROOT, CURRENT> StringAssertions<ROOT, CURRENT> of(
        Function<@NotNull CURRENT, @Nullable String> extract,
        Rules<ROOT> rules,
        NullSpec<ROOT, CURRENT> parent) {
      return new StringAssertions<>() {

        @Override
        public Rules<ROOT> rules() {
          return rules;
        }

        @Override
        public StringAssertions<ROOT, CURRENT> isEmpty() {
          return StringAssertions.of(
              extract,
              rules.add(t -> Optional.empty()),
              parent
          );
        }

        @Override
        public StringAssertions<ROOT, CURRENT> isNotBlank() {
          return this;
        }

        @Override
        public StringAssertions<ROOT, CURRENT> assertLength(Integer length) {
          return this;
        }

        @Override
        public StringAssertions<ROOT, CURRENT> hasMinLength(Integer minLength) {
          return this;
        }

        @Override
        public StringAssertions<ROOT, CURRENT> hasMaxLength(Integer maxLength) {
          return this;
        }

        @Override
        public StringAssertions<ROOT, CURRENT> hasLengthRange(Integer minLength, Integer maxLength) {
          return this;
        }

        @Override
        public StringAssertions<ROOT, CURRENT> matches(Pattern pattern) {
          return this;
        }
      };
    }

  }

  interface Violation {

    String field();

    String message();

    static Violation of(String message) {
      return of("", message);
    }

    static Violation of(String field, String message) {
      Objects.requireNonNull(field);
      Objects.requireNonNull(message);
      return new Violation() {

        @Override
        public String field() {
          return field;
        }

        @Override
        public String message() {
          return message;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
          if (this == obj) {
            return true;
          }
          if (obj == null || getClass() != obj.getClass()) {
            return false;
          }
          var that = (Violation) obj;
          return Objects.equals(field, that.field()) && Objects.equals(message, that.message());
        }

        @Override
        public int hashCode() {
          return Objects.hash(field, message);
        }

        @Override
        public String toString() {
          return "Violation[" + "field='" + field + '\'' + ", message='" + message + '\'' + ']';
        }
      };
    }
  }

  interface Child<SELF, PARENT> {

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

  private static <IN, OUT> Function<@NotNull IN, @NotNull Optional<OUT>> wrapOptional(
      Function<@NotNull IN, @Nullable OUT> fn) {
    return t -> Optional.ofNullable(fn.apply(t));
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> T hide(Throwable t) throws T {
    throw (T) t;
  }

  private static <T> List<T> mergeLists(List<T> left, List<T> right) {
    if (left.isEmpty()) {
      return right;
    } else if (right.isEmpty()) {
      return left;
    }
    var resultArray = new Object[left.size() + right.size()];
    for (var idx = 0; idx < left.size(); idx++) {
      resultArray[idx] = Objects.requireNonNull(left.get(idx));
    }
    for (var idx = 0; idx < right.size(); idx++) {
      resultArray[idx + left.size()] = Objects.requireNonNull(right.get(idx));
    }
    @SuppressWarnings("unchecked")
    T[] result = (T[]) resultArray;
    return List.of(result);
  }

  private static <T> List<T> addToList(List<T> left, T right) {
    return mergeLists(left, List.of(right));
  }
}
