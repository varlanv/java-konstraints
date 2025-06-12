package com.varlanv.konstraints;

import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface Valid<T> {

  <R> Valid<R> map(Function<T, R> mapper);

  <R> Valid<R> flatMap(Function<T, Valid<R>> mapper);

  T orElseThrow(Supplier<? extends Throwable> exceptionSupplier);

  T orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn);

  Valid<T> switchIfInvalid(Supplier<Valid<T>> supplier);

  Optional<T> optional();

  boolean isValid();

  boolean isNotValid();

  @Unmodifiable
  List<Violation> violations();

  static <T> Valid<T> ofValid(T value) {
    Objects.requireNonNull(value);
    return new AlwaysValid<>(() -> value);
  }

  static <T> Valid<T> ofValid(Supplier<@NotNull T> supplier) {
    return new AlwaysValid<>(Objects.requireNonNull(supplier));
  }

  static <T> Valid<T> ofInvalid(List<Violation> violations) {
    if (violations.isEmpty()) {
      throw new IllegalArgumentException("Violations must not be empty");
    }
    return new Invalid<>(List.copyOf(violations));
  }

  static <T> ValidationSpec<T> validationSpec(
      Function<AssertionsSpec<T, T>, AssertionsSpec<T, ?>> specAction) {
    return ValidationSpec.fromFn(specAction);
  }

  interface ValidationSpec<T> {

    Function<T, Valid<T>> toValidationFunction();

    UnaryOperator<T> toValidationFunction(
        Function<List<Violation>, ? extends Throwable> onException);

    UnaryOperator<T> toValidationFunction(Supplier<? extends Throwable> onException);

    Valid<T> validate(T t);

    static <T> ValidationSpec<T> fromFn(
        Function<AssertionsSpec<T, T>, AssertionsSpec<T, ?>> specAction) {
      Objects.requireNonNull(specAction);
      return new ValidationSpec<>() {
        @Override
        public Function<T, Valid<T>> toValidationFunction() {
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
        public Valid<T> validate(T t) {
          Objects.requireNonNull(t);
          var violations = new ArrayList<Violation>(1);
          var chain = new AssertionsSpec<T, T>(t, violations);
          specAction.apply(chain);
          if (violations.isEmpty()) {
            return Valid.ofValid(t);
          } else {
            return new Invalid<>(violations);
          }
        }
      };
    }

    static <T> ValidationSpec<T> fromRules(List<Violation> violations) {
      Objects.requireNonNull(violations);
      return new ValidationSpec<>() {

        @Override
        public Function<T, Valid<T>> toValidationFunction() {
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
        public Valid<T> validate(T t) {
          Objects.requireNonNull(t);
          if (violations.isEmpty()) {
            return Valid.ofValid(t);
          } else {
            return new Invalid<>(violations);
          }
        }
      };
    }
  }

  final class IndexedAssertionsSpec<T, N> {

    private final int index;
    private final AssertionsSpec<T, N> delegate;

    public IndexedAssertionsSpec(int index, AssertionsSpec<T, N> delegate) {
      this.index = index;
      this.delegate = delegate;
    }

    public FieldSpec<T, N> field(String fieldName) {
      return new FieldSpec<>(fieldName, delegate);
    }

    public int index() {
      return index;
    }

    public N value() {
      return delegate.value();
    }

    IndexedAssertionsSpec<T, N> withViolation(Violation violation) {
      delegate.violations().add(violation);
      return this;
    }

    List<Violation> violations() {
      return delegate.violations();
    }
  }

  final class AssertionsSpec<T, N> {

    private final Supplier<String> fieldName;
    private final N currentValue;
    private final List<Violation> violations;

    public AssertionsSpec(N currentValue, List<Violation> violations) {
      this(() -> "", currentValue, violations);
    }

    public AssertionsSpec(Supplier<String> fieldName, N currentValue, List<Violation> violations) {
      this.fieldName = fieldName;
      this.currentValue = currentValue;
      this.violations = violations;
    }

    public FieldSpec<T, N> field(String fieldName) {
      return new FieldSpec<>(fieldName, this);
    }

    public AssertionsSpec<T, N> assertCustom(
        Supplier<Boolean> assertion, Supplier<String> message) {
      if (!assertion.get()) {
        return withViolation(Violation.of(fieldName.get(), message.get()));
      }
      return this;
    }

    public N value() {
      return currentValue;
    }

    AssertionsSpec<T, N> withViolation(Violation violation) {
      violations.add(violation);
      return this;
    }

    List<Violation> violations() {
      return violations;
    }
  }

  final class IterableSpec<A, R, I extends Iterable<R>, T, N> {

    private final I value;
    private final String fieldName;
    private final NullSpec<T, N> nullSpec;
    private final BiFunction<Integer, Supplier<Optional<R>>, A> assertionsSupplier;
    @Nullable
    private Supplier<List<R>> listSupplier;

    public IterableSpec(
        I value,
        String fieldName,
        NullSpec<T, N> nullSpec,
        BiFunction<Integer, Supplier<Optional<R>>, A> assertionsSupplier) {
      this.value = value;
      this.fieldName = fieldName;
      this.nullSpec = nullSpec;
      this.assertionsSupplier = assertionsSupplier;
    }

    public I value() {
      return value;
    }

    public AssertionsSpec<T, N> eachItem(Function<A, ?> specAction) {
      var list = list();
      for (int idx = 0, listSize = list.size(); idx < listSize; idx++) {
        var item = list.get(idx);
        if (item != null) {
          specAction.apply(assertionsSupplier.apply(idx, () -> Optional.of(item)));
        }
      }
      return nullSpec.parent();
    }

    public IterableSpec<A, R, I, T, N> assertNoNullValues() {
      var list = list();
      for (int idx = 0, listSize = list.size(); idx < listSize; idx++) {
        var item = list.get(idx);
        if (item == null) {
          nullSpec
              .parent()
              .withViolation(
                  Violation.of(
                      fieldName + "[" + idx + "]",
                      "expected to be non-null item at index [%d]".formatted(idx)));
        }
      }
      return this;
    }

    public IterableSpec<A, R, I, T, N> assertNotEmpty() {
      return check(
          () -> "expected to be null or non-empty",
          () -> "expected to be non-null and non-empty",
          Predicate.not(List::isEmpty));
    }

    public IterableSpec<A, R, I, T, N> assertEmpty() {
      return check(
          () -> "expected to be null or empty",
          () -> "expected to be non-null and empty",
          List::isEmpty);
    }

    public IterableSpec<A, R, I, T, N> assertSize(Integer size) {
      if (size <= 0) {
        throw new IllegalArgumentException("size must be greater than 0");
      }
      return check(
          () -> "expected to be null or have size [%d]".formatted(size),
          () -> "expected to be non-null and have size [%d]".formatted(size),
          rs -> rs.size() == size);
    }

    public IterableSpec<A, R, I, T, N> assertMinSize(Integer minSize) {
      if (minSize <= 0) {
        throw new IllegalArgumentException("minSize must be greater than 0");
      }
      return check(
          () -> "expected to be null or have size at least [%d]".formatted(minSize),
          () -> "expected to be non-null and have size at least [%d]".formatted(minSize),
          rs -> rs.size() >= minSize);
    }

    public IterableSpec<A, R, I, T, N> assertMaxSize(Integer maxSize) {
      if (maxSize <= 0) {
        throw new IllegalArgumentException("maxSize must be greater than 0");
      }
      return check(
          () -> "expected to be null or have size at most [%d]".formatted(maxSize),
          () -> "expected to be non-null and have size at most [%d]".formatted(maxSize),
          rs -> rs.size() <= maxSize);
    }

    public IterableSpec<A, R, I, T, N> assertSizeRange(Integer minSize, Integer maxSize) {
      if (minSize < 0) {
        throw new IllegalArgumentException("minSize must be greater than or equal to 0");
      } else if (maxSize <= 0) {
        throw new IllegalArgumentException("maxSize must be greater than 0");
      } else if (minSize >= maxSize) {
        throw new IllegalArgumentException("minSize must be less than maxSize");
      }
      return check(
          () -> "expected to be null or have size in range [%d - %d]".formatted(minSize, maxSize),
          () ->
              "expected to be non-null or have size in range [%d - %d]".formatted(minSize, maxSize),
          rs -> rs.size() >= minSize && rs.size() <= maxSize);
    }

    private IterableSpec<A, R, I, T, N> check(
        Supplier<String> nullableMessage,
        Supplier<String> nonNullMessage,
        Predicate<@NotNull List<R>> condition) {
      var list = list();
      if (!condition.test(list)) {
        if (!nullSpec.allowNull()) {
          nullSpec.parent().withViolation(Violation.of(fieldName, nonNullMessage.get()));
        } else {
          nullSpec.parent().withViolation(Violation.of(fieldName, nullableMessage.get()));
        }
      }
      return this;
    }

    private List<@Nullable R> list() {
      var supplier = listSupplier;
      if (supplier == null) {
        List<R> list = null;
        for (var item : value) {
          if (list == null) {
            list = new ArrayList<>();
          }
          list.add(item);
        }
        if (list == null) {
          supplier = List::of;
        } else {
          var objects = Collections.unmodifiableList(list);
          supplier = () -> objects;
        }
        listSupplier = supplier;
      }
      return supplier.get();
    }
  }

  final class IterablePickSpec<T, N> {

    private final NullSpec<T, N> nullSpec;

    public IterablePickSpec(NullSpec<T, N> nullSpec) {
      this.nullSpec = nullSpec;
    }

    public <I extends Iterable<String>> AssertionsSpec<T, N> strings(
        Function<@NotNull N, @Nullable I> valueFn,
        Function<IterableSpec<IndexedStringAssertions<T, N>, String, I, T, N>, AssertionsSpec<T, N>>
            specAction) {
      @Nullable var value = valueFn.apply(nullSpec.parent().value());
      if (value != null) {
        specAction.apply(
            new IterableSpec<>(
                value,
                nullSpec.parent.parent.fieldName.get(),
                nullSpec,
                (idx, val) ->
                    IndexedStringAssertions.forValue(
                        idx,
                        StringAssertions.forValue(
                            val.get().get(),
                            () ->
                                nullSpec.parent.parent.fieldName.get()
                                    + "."
                                    + nullSpec.parent.fieldName
                                    + "["
                                    + idx
                                    + "]",
                            nullSpec))));
      }
      return nullSpec.parent();
    }

    public <R extends Comparable<R>, I extends Iterable<R>> AssertionsSpec<T, N> numbers(
        Function<@NotNull N, @Nullable I> valueFn,
        Function<IterableSpec<IndexedNumberAssertions<R, T, N>, R, I, T, N>, AssertionsSpec<T, N>>
            specAction) {
      @Nullable var value = valueFn.apply(nullSpec.parent().value());
      if (value != null) {
        specAction.apply(
            new IterableSpec<>(
                value,
                nullSpec.parent.parent.fieldName.get(),
                nullSpec,
                (idx, val) ->
                    IndexedNumberAssertions.forValue(
                        idx,
                        NumberAssertions.forValue(
                            val.get().get(),
                            () ->
                                nullSpec.parent.parent.fieldName.get()
                                    + "."
                                    + nullSpec.parent.fieldName
                                    + "["
                                    + idx
                                    + "]",
                            nullSpec))));
      }
      return nullSpec.parent();
    }

    public <R, I extends Iterable<R>> AssertionsSpec<T, N> nested(
        Function<@NotNull N, @Nullable I> valueFn,
        Function<IterableSpec<IndexedAssertionsSpec<T, R>, R, I, T, N>, AssertionsSpec<T, N>>
            specAction) {
      @Nullable var value = valueFn.apply(nullSpec.parent().value());
      if (value != null) {
        specAction.apply(
            new IterableSpec<>(
                value,
                nullSpec.parent.parent.fieldName.get(),
                nullSpec,
                (idx, val) ->
                    new IndexedAssertionsSpec<>(
                        idx,
                        new AssertionsSpec<>(
                            () ->
                                nullSpec.parent.parent.fieldName.get()
                                    + "."
                                    + nullSpec.parent.fieldName
                                    + "["
                                    + idx
                                    + "]",
                            val.get().get(),
                            nullSpec.parent().violations()))));
      }
      return nullSpec.parent();
    }
  }

  final class FieldSpec<T, N> {

    private final String fieldName;
    private final AssertionsSpec<T, N> parent;

    public FieldSpec(String fieldName, AssertionsSpec<T, N> parent) {
      this.fieldName = fieldName;
      this.parent = parent;
    }

    public NullSpec<T, N> nullable() {
      return new NullSpec<>(true, this);
    }

    public NullSpec<T, N> nonNull() {
      return new NullSpec<>(false, this);
    }

    public AssertionsSpec<T, N> assertNull(Function<@NotNull N, @Nullable Object> valueFn) {
      if (valueFn.apply(parent.value()) != null) {
        return parent.withViolation(Violation.of(fieldName, "expected to be null"));
      } else {
        return parent;
      }
    }

    public AssertionsSpec<T, N> assertNotNull(Function<@NotNull N, @Nullable Object> valueFn) {
      if (valueFn.apply(parent.value()) == null) {
        return parent.withViolation(Violation.of(fieldName, "expected to be non-null"));
      } else {
        return parent;
      }
    }
  }

  final class NullSpec<T, N> {

    private final Boolean allowNull;
    private final FieldSpec<T, N> parent;

    public NullSpec(Boolean allowNull, FieldSpec<T, N> parent) {
      this.allowNull = allowNull;
      this.parent = parent;
    }

    public AssertionsSpec<T, N> string(
        Function<@NotNull N, @Nullable String> valueFn,
        Function<StringAssertions<T, N>, StringAssertions<T, N>> specAction) {
      @Nullable var val = valueFn.apply(parent.parent.value());
      if (val != null) {
        specAction.apply(
            StringAssertions.forValue(
                val, fieldNameFormatSupplier(parent.parent.fieldName, parent.fieldName), this));
      }
      return parent();
    }

    public <R extends Comparable<R>> AssertionsSpec<T, N> number(
        Function<@NotNull N, @Nullable R> valueFn,
        Function<NumberAssertions<R, T, N>, NumberAssertions<R, T, N>> specAction) {
      @Nullable var val = valueFn.apply(parent.parent.value());
      if (val != null) {
        specAction.apply(
            NumberAssertions.forValue(
                val, fieldNameFormatSupplier(parent.parent.fieldName, parent.fieldName), this));
      }
      return parent();
    }

    public IterablePickSpec<T, N> iterable() {
      return new IterablePickSpec<>(this);
    }

    public <R> AssertionsSpec<T, N> nested(
        Function<@NotNull N, @Nullable R> valueFn,
        Function<AssertionsSpec<T, R>, AssertionsSpec<T, R>> specAction) {
      @Nullable var val = valueFn.apply(parent.parent.value());
      if (val != null) {
        specAction.apply(
            new AssertionsSpec<>(
                fieldNameFormatSupplier(parent.parent.fieldName, parent.fieldName),
                val,
                parent.parent.violations()));
      }
      return parent();
    }

    Boolean allowNull() {
      return allowNull;
    }

    AssertionsSpec<T, N> parent() {
      return parent.parent;
    }
  }

  interface IndexedNumberAssertions<R extends Comparable<R>, T, N>
      extends NumberAssertions<R, T, N> {

    int index();

    IndexedNumberAssertions<R, T, N> assertGte(R target);

    IndexedNumberAssertions<R, T, N> assertLte(R target);

    IndexedNumberAssertions<R, T, N> assertInRange(R minTarget, R maxTarget);

    static <R extends Comparable<R>, T, N> IndexedNumberAssertions<R, T, N> forValue(
        int index, NumberAssertions<R, T, N> delegate) {
      return new IndexedNumberAssertions<>() {
        @Override
        public R value() {
          return delegate.value();
        }

        @Override
        public IndexedNumberAssertions<R, T, N> assertGte(R target) {
          delegate.assertGte(target);
          return this;
        }

        @Override
        public IndexedNumberAssertions<R, T, N> assertLte(R target) {
          delegate.assertLte(target);
          return this;
        }

        @Override
        public IndexedNumberAssertions<R, T, N> assertInRange(R minTarget, R maxTarget) {
          delegate.assertInRange(minTarget, maxTarget);
          return this;
        }

        @Override
        public IndexedNumberAssertions<R, T, N> assertIn(Collection<R> set) {
          delegate.assertIn(set);
          return this;
        }

        @Override
        public int index() {
          return index;
        }
      };
    }
  }

  interface NumberAssertions<R extends Comparable<R>, T, N> {

    R value();

    NumberAssertions<R, T, N> assertGte(R target);

    NumberAssertions<R, T, N> assertLte(R target);

    NumberAssertions<R, T, N> assertInRange(R minTarget, R maxTarget);

    NumberAssertions<R, T, N> assertIn(Collection<R> set);

    static <R extends Comparable<R>, T, N> NumberAssertions<R, T, N> forValue(
        R value, Supplier<String> fieldName, NullSpec<T, N> nullSpec) {
      Comparator<R> comparator = Comparable::compareTo;
      return new NumberAssertions<>() {

        @Override
        public R value() {
          return value;
        }

        @Override
        public NumberAssertions<R, T, N> assertGte(R target) {
          Objects.requireNonNull(target);
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must be greater than or equal to [%s]".formatted(target),
              val -> comparator.compare(val, target) >= 0);
        }

        @Override
        public NumberAssertions<R, T, N> assertLte(R target) {
          Objects.requireNonNull(target);
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must be less than or equal to [%s]".formatted(target),
              val -> comparator.compare(val, target) <= 0);
        }

        @Override
        public NumberAssertions<R, T, N> assertInRange(R minTarget, R maxTarget) {
          if (comparator.compare(minTarget, maxTarget) > 0) {
            throw new IllegalArgumentException("minTarget must be less than maxTarget");
          }
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must be in range [%s - %s]".formatted(minTarget, maxTarget),
              val ->
                  comparator.compare(val, minTarget) >= 0
                      && comparator.compare(val, maxTarget) <= 0);
        }

        @Override
        public NumberAssertions<R, T, N> assertIn(Collection<R> set) {
          Objects.requireNonNull(set);
          return check(this, value, nullSpec, fieldName, "value is not allowed", set::contains);
        }
      };
    }
  }

  private static <A, R, T, N> A check(
      A self,
      R val,
      NullSpec<T, N> nullSpec,
      Supplier<String> fieldNameSupplier,
      String nonNullFailMessage,
      Predicate<R> condition) {

    if (!condition.test(val)) {
      nullSpec.parent().withViolation(Violation.of(fieldNameSupplier.get(), nonNullFailMessage));
      return self;
    }
    return self;
  }

  interface IndexedStringAssertions<T, N> extends StringAssertions<T, N> {

    int index();

    IndexedStringAssertions<T, N> assertEmpty();

    IndexedStringAssertions<T, N> assertNotBlank();

    IndexedStringAssertions<T, N> assertLength(Integer length);

    IndexedStringAssertions<T, N> assertMinLength(Integer minLength);

    IndexedStringAssertions<T, N> assertMaxLength(Integer maxLength);

    IndexedStringAssertions<T, N> assertLengthRange(Integer minLength, Integer maxLength);

    IndexedStringAssertions<T, N> assertMatches(Pattern pattern);

    <E extends Enum<E>> IndexedStringAssertions<T, N> assertInEnum(Class<E> enumValue);

    <E extends Enum<E>> IndexedStringAssertions<T, N> assertInEnum(
        Class<E> enumValue, Function<E, String> enumValueToNameFn);

    IndexedStringAssertions<T, N> assertValidUUID();

    static <T, N> IndexedStringAssertions<T, N> forValue(
        int index, StringAssertions<T, N> delegate) {
      return new IndexedStringAssertions<>() {

        @Override
        public int index() {
          return index;
        }

        @Override
        public String value() {
          return delegate.value();
        }

        @Override
        public IndexedStringAssertions<T, N> assertEmpty() {
          delegate.assertEmpty();
          return this;
        }

        @Override
        public IndexedStringAssertions<T, N> assertNotBlank() {
          delegate.assertNotBlank();
          return this;
        }

        @Override
        public IndexedStringAssertions<T, N> assertLength(Integer length) {
          delegate.assertLength(length);
          return this;
        }

        @Override
        public IndexedStringAssertions<T, N> assertMinLength(Integer minLength) {
          delegate.assertMinLength(minLength);
          return this;
        }

        @Override
        public IndexedStringAssertions<T, N> assertMaxLength(Integer maxLength) {
          delegate.assertMaxLength(maxLength);
          return this;
        }

        @Override
        public IndexedStringAssertions<T, N> assertLengthRange(
            Integer minLength, Integer maxLength) {
          delegate.assertLengthRange(minLength, maxLength);
          return this;
        }

        @Override
        public IndexedStringAssertions<T, N> assertMatches(Pattern pattern) {
          delegate.assertMatches(pattern);
          return this;
        }

        @Override
        public IndexedStringAssertions<T, N> assertValidUUID() {
          delegate.assertValidUUID();
          return this;
        }

        @Override
        public <E extends Enum<E>> IndexedStringAssertions<T, N> assertInEnum(Class<E> enumValue) {
          delegate.assertInEnum(enumValue);
          return this;
        }

        @Override
        public <E extends Enum<E>> IndexedStringAssertions<T, N> assertInEnum(
            Class<E> enumValue, Function<E, String> enumValueToNameFn) {
          delegate.assertInEnum(enumValue, enumValueToNameFn);
          return this;
        }
      };
    }
  }

  interface StringAssertions<T, N> {

    String value();

    StringAssertions<T, N> assertEmpty();

    StringAssertions<T, N> assertNotBlank();

    StringAssertions<T, N> assertLength(Integer length);

    StringAssertions<T, N> assertMinLength(Integer minLength);

    StringAssertions<T, N> assertMaxLength(Integer maxLength);

    StringAssertions<T, N> assertLengthRange(Integer minLength, Integer maxLength);

    StringAssertions<T, N> assertMatches(Pattern pattern);

    StringAssertions<T, N> assertValidUUID();

    <E extends Enum<E>> StringAssertions<T, N> assertInEnum(Class<E> enumValue);

    <E extends Enum<E>> StringAssertions<T, N> assertInEnum(
        Class<E> enumValue, Function<E, String> enumValueToNameFn);

    static <T, N> StringAssertions<T, N> forValue(
        String value, Supplier<String> fieldName, NullSpec<T, N> nullSpec) {

      return new StringAssertions<>() {

        @Override
        public String value() {
          return value;
        }

        @Override
        public StringAssertions<T, N> assertEmpty() {
          return check(this, value, nullSpec, fieldName, "must be empty string", String::isEmpty);
        }

        @Override
        public StringAssertions<T, N> assertNotBlank() {
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must be non-blank string",
              Predicate.not(String::isBlank));
        }

        @Override
        public StringAssertions<T, N> assertLength(Integer length) {
          Objects.requireNonNull(length);
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must have length [%d]".formatted(length),
              val -> val.length() == length);
        }

        @Override
        public StringAssertions<T, N> assertMinLength(Integer minLength) {
          Objects.requireNonNull(minLength);
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must have min length [%d]".formatted(minLength),
              val -> val.length() >= minLength);
        }

        @Override
        public StringAssertions<T, N> assertMaxLength(Integer maxLength) {
          Objects.requireNonNull(maxLength);
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must  have max length [%d]".formatted(maxLength),
              val -> val.length() <= maxLength);
        }

        @Override
        public StringAssertions<T, N> assertLengthRange(Integer minLength, Integer maxLength) {
          Objects.requireNonNull(minLength);
          Objects.requireNonNull(maxLength);
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must have length range [%d - %d]".formatted(minLength, maxLength),
              val -> val.length() >= minLength && val.length() <= maxLength);
        }

        @Override
        public StringAssertions<T, N> assertMatches(Pattern pattern) {
          Objects.requireNonNull(pattern);
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must match pattern [%s]".formatted(pattern.pattern()),
              val -> pattern.matcher(val).matches());
        }

        @Override
        public StringAssertions<T, N> assertValidUUID() {
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must be valid UUID",
              val ->
                  val.length() == 36
                      && val.charAt(8) == '-'
                      && val.charAt(13) == '-'
                      && val.charAt(18) == '-'
                      && val.charAt(23) == '-');
        }

        @Override
        public <E extends Enum<E>> StringAssertions<T, N> assertInEnum(Class<E> enumType) {
          return assertInEnum(enumType, Enum::name);
        }

        @Override
        public <E extends Enum<E>> StringAssertions<T, N> assertInEnum(
            Class<E> enumType, Function<E, String> enumValueToNameFn) {
          Objects.requireNonNull(enumType);
          Objects.requireNonNull(enumValueToNameFn);
          return check(
              this,
              value,
              nullSpec,
              fieldName,
              "must be in list of allowed enum values",
              val -> {
                var enumConstants = enumType.getEnumConstants();
                for (var enumConstant : enumConstants) {
                  if (enumValueToNameFn.apply(enumConstant).equals(val)) {
                    return true;
                  }
                }
                return false;
              });
        }
      };
    }
  }

  final class NullAssertions<R, T, N> {

    private final N currentValue;
    private final Function<@NotNull N, @NotNull Optional<R>> valueFn;
    private final Supplier<String> fieldNameSupplier;
    private final BiFunction<String, Boolean, String> messageFn;
    private final Function<@NotNull R, Boolean> condition;
    private final AssertionsSpec<T, N> parent;

    public NullAssertions(
        N currentValue,
        Function<@NotNull N, @NotNull Optional<R>> valueFn,
        Supplier<String> fieldNameSupplier,
        BiFunction<String, Boolean, String> messageFn,
        Function<@NotNull R, Boolean> condition,
        AssertionsSpec<T, N> parent) {
      this.currentValue = currentValue;
      this.valueFn = valueFn;
      this.fieldNameSupplier = fieldNameSupplier;
      this.messageFn = messageFn;
      this.condition = condition;
      this.parent = parent;
    }

    public AssertionsSpec<T, N> allowingNull() {
      var val = valueFn.apply(currentValue);
      if (val.isPresent() && !condition.apply(val.get())) {
        var fieldName = fieldNameSupplier.get();
        return parent.withViolation(Violation.of(fieldName, messageFn.apply(fieldName, true)));
      }
      return parent;
    }

    public AssertionsSpec<T, N> rejectingNull() {
      var val = valueFn.apply(currentValue);
      if (val.isEmpty() || !condition.apply(val.get())) {
        var fieldName = fieldNameSupplier.get();
        return parent.withViolation(Violation.of(fieldName, messageFn.apply(fieldName, false)));
      }
      return parent;
    }
  }

  final class Invalid<T> implements Valid<T> {

    private final List<Violation> violations;

    public Invalid(List<Violation> violations) {
      this.violations = violations;
    }

    @Override
    public <R> Valid<R> map(Function<T, R> mapper) {
      return self();
    }

    @Override
    public <R> Valid<R> flatMap(Function<T, Valid<R>> mapper) {
      return self();
    }

    @Override
    public T orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
      throw Valid.hide(exceptionSupplier.get());
    }

    @Override
    public T orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn) {
      throw Valid.hide(exceptionFn.apply(Collections.unmodifiableList(violations)));
    }

    @Override
    public Valid<T> switchIfInvalid(Supplier<Valid<T>> supplier) {
      return supplier.get();
    }

    @Override
    public Optional<T> optional() {
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
      return Collections.unmodifiableList(violations);
    }

    private <R> Valid<R> self() {
      @SuppressWarnings("unchecked")
      var self = (Valid<R>) this;
      return self;
    }
  }

  final class AlwaysValid<T> implements Valid<T> {

    private final Supplier<T> value;

    public AlwaysValid(Supplier<T> value) {
      this.value = value;
    }

    @Override
    public <R> Valid<R> map(Function<T, R> mapper) {
      return new AlwaysValid<>(() -> mapper.apply(value.get()));
    }

    @Override
    public <R> Valid<R> flatMap(Function<T, Valid<R>> mapper) {
      return mapper.apply(value.get());
    }

    @Override
    public T orElseThrow(Supplier<? extends Throwable> exceptionSupplier) {
      return value.get();
    }

    @Override
    public T orElseThrow(Function<List<Violation>, ? extends Throwable> exceptionFn) {
      return value.get();
    }

    @Override
    public Valid<T> switchIfInvalid(Supplier<Valid<T>> supplier) {
      return this;
    }

    @Override
    public Optional<T> optional() {
      return Optional.of(value.get());
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

  private static <T, R> Function<@NotNull T, @NotNull Optional<R>> wrapOptional(
      Function<@NotNull T, @Nullable R> fn) {
    return t -> Optional.ofNullable(fn.apply(t));
  }

  private static <T, R>
  Function<@NotNull T, @NotNull Supplier<@NotNull Optional<R>>> wrapOptionalSupplier(
      Function<@NotNull T, @Nullable R> fn) {
    return t -> () -> Optional.ofNullable(fn.apply(t));
  }

  private static String fieldNameFormat(String currentObjectField, String field) {
    return currentObjectField.isEmpty() ? field : currentObjectField + "." + field;
  }

  private static Supplier<String> fieldNameFormatSupplier(
      Supplier<String> currentObjectField, String field) {
    return () -> fieldNameFormat(currentObjectField.get(), field);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> T hide(Throwable t) throws T {
    throw (T) t;
  }
}
