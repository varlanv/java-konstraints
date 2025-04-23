package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
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
      Function<AssertionsSpec<SUBJECT, SUBJECT>, AssertionsSpec<SUBJECT, ?>> specAction) {
    return ValidationSpec.fromRules(
        specAction.apply(
                AssertionsSpec.from(
                    Rules.create(),
                    Function.identity()
                )
            )
            .rules()
    );
  }

  interface Rules<SUBJECT> {

    Rules<SUBJECT> add(Rule<SUBJECT> rule);

    Rules<SUBJECT> merge(Rules<SUBJECT> other);

    @Unmodifiable
    List<Violation> apply(SUBJECT t);

    List<Rule<SUBJECT>> list();

    static <SUBJECT> Rules<SUBJECT> create() {
      var rules = new ArrayList<Rule<SUBJECT>>();
      var view = Collections.unmodifiableList(rules);
      return new Rules<>() {

        @Override
        public Rules<SUBJECT> add(Rule<SUBJECT> rule) {
          rules.add(Objects.requireNonNull(rule));
          return this;
        }

        @Override
        public Rules<SUBJECT> merge(Rules<SUBJECT> other) {
          for (Rule<SUBJECT> subjectRule : other.list()) {
            add(Objects.requireNonNull(subjectRule));
          }
          return this;
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
          return view;
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

  interface AssertionsSpec<PREVIOUS, CURRENT> {

    FieldSpec<PREVIOUS, CURRENT> field(String fieldName);

    AssertionsSpec<PREVIOUS, CURRENT> withRule(Rule<PREVIOUS> rule);

    Rules<PREVIOUS> rules();

    Function<@NotNull PREVIOUS, @Nullable CURRENT> currentNestFn();

    static <PREVIOUS, CURRENT> AssertionsSpec<PREVIOUS, CURRENT> from(
        Rules<PREVIOUS> rules, Function<@NotNull PREVIOUS, @Nullable CURRENT> currentNestFn) {
      return new AssertionsSpec<>() {

        @Override
        public FieldSpec<PREVIOUS, CURRENT> field(String fieldName) {
          return FieldSpec.of(fieldName, this);
        }

        @Override
        public AssertionsSpec<PREVIOUS, CURRENT> withRule(Rule<PREVIOUS> rule) {
          rules.add(rule);
          return this;
        }

        @Override
        public Rules<PREVIOUS> rules() {
          return rules;
        }

        @Override
        public Function<@NotNull PREVIOUS, @Nullable CURRENT> currentNestFn() {
          return currentNestFn;
        }
      };
    }
  }

  interface FieldSpec<PREVIOUS, CURRENT> {

    <TARGET> AssertionsSpec<PREVIOUS, CURRENT> assertNotNull(Function<CURRENT, TARGET> extract);

    <TARGET> AssertionsSpec<PREVIOUS, CURRENT> assertNull(Function<CURRENT, TARGET> extract);

    NullSpec<PREVIOUS, CURRENT> nullable();

    NullSpec<PREVIOUS, CURRENT> nonNull();

    String fieldName();

    AssertionsSpec<PREVIOUS, CURRENT> parent();

    static <PREVIOUS, CURRENT> FieldSpec<PREVIOUS, CURRENT> of(
        String fieldName, AssertionsSpec<PREVIOUS, CURRENT> parent) {
      return new FieldSpec<>() {

        @Override
        public <TARGET> AssertionsSpec<PREVIOUS, CURRENT> assertNotNull(
            Function<CURRENT, TARGET> extract) {
          return parent().withRule(
              t -> {
                if (t == null) {
                  return Optional.of(Violation.of(fieldName(), "expected to be non-null"));
                } else {
                  return Optional.empty();
                }
              });
        }

        @Override
        public <TARGET> AssertionsSpec<PREVIOUS, CURRENT> assertNull(
            Function<CURRENT, TARGET> extract) {
          return parent().withRule(
              t -> {
                if (t != null) {
                  return Optional.of(Violation.of(fieldName(), "expected to be null"));
                } else {
                  return Optional.empty();
                }
              });
        }

        @Override
        public NullSpec<PREVIOUS, CURRENT> nullable() {
          return NullSpec.of(true, this);
        }

        @Override
        public NullSpec<PREVIOUS, CURRENT> nonNull() {
          return NullSpec.of(false, this);
        }

        @Override
        public String fieldName() {
          return fieldName;
        }

        @Override
        public AssertionsSpec<PREVIOUS, CURRENT> parent() {
          return parent;
        }
      };
    }
  }

  interface NullSpec<PREVIOUS, CURRENT> {

    AssertionsSpec<PREVIOUS, CURRENT> string(
        Function<@NotNull CURRENT, @Nullable String> extract,
        Function<@NotNull StringAssertions<PREVIOUS, CURRENT>, ?> action);

    <TARGET extends Comparable<TARGET>> AssertionsSpec<PREVIOUS, CURRENT> number(
        Function<@NotNull CURRENT, @Nullable TARGET> extract,
        Function<@NotNull NumberAssertions<TARGET, PREVIOUS, CURRENT>, ?> action);

    <TARGET> AssertionsSpec<PREVIOUS, CURRENT> nested(Function<@NotNull CURRENT, @Nullable TARGET> extract,
                                                      Function<@NotNull AssertionsSpec<PREVIOUS, TARGET>, ?> action);

    FieldSpec<PREVIOUS, CURRENT> parent();

    IterableSpec<PREVIOUS, CURRENT> iterable();

    Boolean allowNull();

    static <PREVIOUS, CURRENT> NullSpec<PREVIOUS, CURRENT> of(Boolean allowNull, FieldSpec<PREVIOUS, CURRENT> parent) {
      return new NullSpec<>() {
        @Override
        public AssertionsSpec<PREVIOUS, CURRENT> string(
            Function<@NotNull CURRENT, @Nullable String> extract,
            Function<@NotNull StringAssertions<PREVIOUS, CURRENT>, ?> action) {
          StringAssertions.of(extract, action, this);
          return parent().parent();
        }

        @Override
        public <TARGET extends Comparable<TARGET>> AssertionsSpec<PREVIOUS, CURRENT> number(
            Function<@NotNull CURRENT, @Nullable TARGET> extract,
            Function<@NotNull NumberAssertions<TARGET, PREVIOUS, CURRENT>, ?> action) {
          NumberAssertions.of(extract, action, this);
          return parent().parent();
        }

        @Override
        public <TARGET> AssertionsSpec<PREVIOUS, CURRENT> nested(
            Function<@NotNull CURRENT, @Nullable TARGET> extract,
            Function<@NotNull AssertionsSpec<PREVIOUS, TARGET>, ?> action) {
          var parent = parent().parent();
          AssertionsSpec.<PREVIOUS, TARGET>from(
              parent.rules(),
              t -> {
                CURRENT apply = parent().parent().currentNestFn().apply(t);
                return apply == null ? null : extract.apply(apply);
              }
          );
          return parent;
        }

        @Override
        public FieldSpec<PREVIOUS, CURRENT> parent() {
          return parent;
        }

        @Override
        public IterableSpec<PREVIOUS, CURRENT> iterable() {
          return IterableSpec.of(this);
        }

        @Override
        public Boolean allowNull() {
          return allowNull;
        }
      };
    }
  }

  interface IterableSpec<PREVIOUS, CURRENT> {

    <ITEM extends CharSequence, ITEMS extends Iterable<ITEM>>
    IterableAssertions<StringAssertions<PREVIOUS, CURRENT>, ITEM, ITEMS, PREVIOUS, CURRENT> strings(
        Function<@NotNull CURRENT, @Nullable ITEMS> extract);

    <ITEM extends Comparable<ITEM>, ITEMS extends Iterable<ITEM>>
    IterableAssertions<NumberAssertions<ITEM, PREVIOUS, CURRENT>, ITEM, ITEMS, PREVIOUS, CURRENT> numbers(
        Function<@NotNull CURRENT, @Nullable ITEMS> extract
    );

    <ITEM, ITEMS extends Iterable<ITEM>>
    IterableAssertions<AssertionsSpec<PREVIOUS, ITEM>, ITEM, ITEMS, PREVIOUS, CURRENT> nested(
        Function<@NotNull CURRENT, @Nullable ITEMS> extract);

    NullSpec<PREVIOUS, CURRENT> parent();

    static <PREVIOUS, CURRENT> IterableSpec<PREVIOUS, CURRENT> of(NullSpec<PREVIOUS, CURRENT> nullSpec) {
      return new IterableSpec<>() {
        @Override
        public <ITEM extends CharSequence, ITEMS extends Iterable<ITEM>>
        IterableAssertions<StringAssertions<PREVIOUS, CURRENT>, ITEM, ITEMS, PREVIOUS, CURRENT> strings(
            Function<@NotNull CURRENT, @Nullable ITEMS> extract) {
          return IterableAssertions.of(extract, this);
        }

        @Override
        public <ITEM extends Comparable<ITEM>, ITEMS extends Iterable<ITEM>>
        IterableAssertions<NumberAssertions<ITEM, PREVIOUS, CURRENT>, ITEM, ITEMS, PREVIOUS, CURRENT> numbers(
            Function<@NotNull CURRENT, @Nullable ITEMS> extract) {
          return IterableAssertions.of(extract, this);
        }

        @Override
        public <ITEM, ITEMS extends Iterable<ITEM>>
        IterableAssertions<AssertionsSpec<PREVIOUS, ITEM>, ITEM, ITEMS, PREVIOUS, CURRENT> nested(
            Function<@NotNull CURRENT, @Nullable ITEMS> extract) {
          return IterableAssertions.of(extract, this);
        }

        @Override
        public NullSpec<PREVIOUS, CURRENT> parent() {
          return nullSpec;
        }
      };
    }
  }

  interface IterableAssertions<ASSERTIONS, ITEM, ITEMS extends Iterable<ITEM>, PREVIOUS, CURRENT> {

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertSize(Integer size);

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertMinSize(Integer minSize);

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertMaxSize(Integer maxSize);

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertSizeRange(Integer minSize, Integer maxSize);

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertNotEmpty();

    IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertEmpty();

    AssertionsSpec<PREVIOUS, CURRENT> eachItem(Function<ASSERTIONS, ?> action);

    static <
        ASSERTIONS,
        ITEM,
        ITEMS extends Iterable<ITEM>,
        PREVIOUS,
        CURRENT> IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> of(
        Function<@NotNull CURRENT, @Nullable ITEMS> extract, IterableSpec<PREVIOUS, CURRENT> parent) {
      return new IterableAssertions<>() {
        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertSize(Integer size) {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertMinSize(Integer minSize) {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertMaxSize(Integer maxSize) {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertSizeRange(Integer minSize, Integer maxSize) {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertNotEmpty() {
          return this;
        }

        @Override
        public IterableAssertions<ASSERTIONS, ITEM, ITEMS, PREVIOUS, CURRENT> assertEmpty() {
          return this;
        }

        @Override
        public AssertionsSpec<PREVIOUS, CURRENT> eachItem(Function<ASSERTIONS, ?> action) {
          return parent.parent().parent().parent();
        }
      };
    }
  }

  interface NumberAssertions<TARGET extends Comparable<TARGET>, PREVIOUS, CURRENT> {


    NumberAssertions<TARGET, PREVIOUS, CURRENT> isGte(TARGET target);

    NumberAssertions<TARGET, PREVIOUS, CURRENT> isLte(TARGET target);

    NumberAssertions<TARGET, PREVIOUS, CURRENT> isInRange(TARGET minTarget, TARGET maxTarget);

    static <TARGET extends Comparable<TARGET>, PREVIOUS, CURRENT> NumberAssertions<TARGET, PREVIOUS, CURRENT> of(
        Function<@NotNull CURRENT, @Nullable TARGET> extract,
        Function<@NotNull NumberAssertions<TARGET, PREVIOUS, CURRENT>, ?> action,
        NullSpec<PREVIOUS, CURRENT> parent) {
      return new NumberAssertions<>() {

        @Override
        public NumberAssertions<TARGET, PREVIOUS, CURRENT> isGte(TARGET target) {
          return this;
        }

        @Override
        public NumberAssertions<TARGET, PREVIOUS, CURRENT> isLte(TARGET target) {
          return this;
        }

        @Override
        public NumberAssertions<TARGET, PREVIOUS, CURRENT> isInRange(
            TARGET minTarget, TARGET maxTarget) {
          return this;
        }
      };
    }

  }

  interface StringAssertions<PREVIOUS, CURRENT> {

    StringAssertions<PREVIOUS, CURRENT> isEmpty();

    StringAssertions<PREVIOUS, CURRENT> isNotBlank();

    StringAssertions<PREVIOUS, CURRENT> assertLength(Integer length);

    StringAssertions<PREVIOUS, CURRENT> hasMinLength(Integer minLength);

    StringAssertions<PREVIOUS, CURRENT> hasMaxLength(Integer maxLength);

    StringAssertions<PREVIOUS, CURRENT> hasLengthRange(Integer minLength, Integer maxLength);

    StringAssertions<PREVIOUS, CURRENT> matches(Pattern pattern);

    static <PREVIOUS, CURRENT> StringAssertions<PREVIOUS, CURRENT> of(
        Function<@NotNull CURRENT, String> extract,
        Function<@NotNull StringAssertions<PREVIOUS, CURRENT>, ?> action,
        NullSpec<PREVIOUS, CURRENT> parent) {
      return new StringAssertions<>() {

        @Override
        public StringAssertions<PREVIOUS, CURRENT> isEmpty() {
          return this;
        }

        @Override
        public StringAssertions<PREVIOUS, CURRENT> isNotBlank() {
          return this;
        }

        @Override
        public StringAssertions<PREVIOUS, CURRENT> assertLength(Integer length) {
          return this;
        }

        @Override
        public StringAssertions<PREVIOUS, CURRENT> hasMinLength(Integer minLength) {
          return this;
        }

        @Override
        public StringAssertions<PREVIOUS, CURRENT> hasMaxLength(Integer maxLength) {
          return this;
        }

        @Override
        public StringAssertions<PREVIOUS, CURRENT> hasLengthRange(
            Integer minLength, Integer maxLength) {
          return this;
        }

        @Override
        public StringAssertions<PREVIOUS, CURRENT> matches(Pattern pattern) {
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

  private static <IN, OUT> Function<@NotNull IN, @NotNull Optional<OUT>> wrapOptional(
      Function<@NotNull IN, @Nullable OUT> fn) {
    return t -> Optional.ofNullable(fn.apply(t));
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> T hide(Throwable t) throws T {
    throw (T) t;
  }
}
