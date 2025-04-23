//package com.varlanv.konstraints;
//
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.regex.Pattern;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ValidTest {
//
//    @Test
//    void stringField__isEmpty__should_correctly_validate_empty_strings_and_null() {
//        record Rec(String strValue) {
//        }
//
//        var spec = Valid.<Rec>validationSpec(chain ->
//                chain.stringField(Rec::strValue, "strValue").isEmpty().allowingNull()
//        );
//
//        assertTrue(spec.validate(new Rec(null)).isValid());
//        assertTrue(spec.validate(new Rec("")).isValid());
//        assertFalse(spec.validate(new Rec("notEmpty")).isValid());
//    }
//
//    @Test
//    void stringField__hasMaxLength__should_correctly_validate_max_length() {
//        record Rec(String strValue) {
//        }
//
//        var spec = Valid.<Rec>validationSpec(chain ->
//                chain.stringField(Rec::strValue, "strValue").hasMaxLength(4).rejectingNull()
//        );
//
//        assertTrue(spec.validate(new Rec("")).isValid());
//        assertTrue(spec.validate(new Rec("test")).isValid());
//        assertFalse(spec.validate(new Rec("exceedLength")).isValid());
//        assertFalse(spec.validate(new Rec(null)).isValid());
//    }
//
//    @Test
//    void chain__multiple_validations__should_correctly_validate_combinations() {
//        record Rec(String name, Integer age) {
//        }
//
//        var spec = Valid.<Rec>validationSpec(chain -> chain
//                .stringField(Rec::name, "name").isNotBlank().rejectingNull()
//                .integerField(Rec::age, "age").isInRange(18, 65).rejectingNull()
//        );
//
//        assertFalse(spec.validate(new Rec("", 20)).isValid());
//        assertFalse(spec.validate(new Rec("ValidName", 70)).isValid());
//        assertTrue(spec.validate(new Rec("Valid", 35)).isValid());
//        assertFalse(spec.validate(new Rec(null, null)).isValid());
//    }
//
//    @Test
//    void stringField__isBlank__orElseThrow_should_throw_with_blank_strings() {
//        record Rec(String strValue) {
//        }
//
//        var exception = new RuntimeException("Invalid value");
//        var function = Valid.<Rec>validationSpec(chain ->
//                chain.stringField(Rec::strValue, "strValue").isNotBlank().rejectingNull()
//        ).toValidationFunction(() -> exception);
//
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec("   ")));
//        assertEquals("nonBlank", function.apply(new Rec("nonBlank")).strValue());
//    }
//
//    @Test
//    void ofInvalid__ifViolationsEmpty__failFast() {
//        var exception = assertThrows(IllegalArgumentException.class, () -> Valid.ofInvalid(List.of()));
//        assertEquals("Violations must not be empty", exception.getMessage());
//    }
//
//    @Test
//    void stringField__hasLength__should_correctly_validate_length() {
//        record Rec(String strValue) {
//        }
//        var function = Valid.<Rec>validationSpec(chain -> chain
//                .stringField(Rec::strValue, "strValue").hasLength(5).rejectingNull()
//        ).toValidationFunction(() -> new RuntimeException("Fail"));
//
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec("123456")));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec("1234")));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec("")));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(null)));
//        assertEquals("12345", function.apply(new Rec("12345")).strValue());
//    }
//
//    @Test
//    void isNull__should_correctly_validate_null() {
//        record Rec(String strValue) {
//        }
//        var spec = Valid.<Rec>validationSpec(chain -> chain.isNull(Rec::strValue, "strValue"));
//
//        assertTrue(spec.validate(new Rec(null)).isValid());
//        assertTrue(spec.validate(new Rec("123")).isNotValid());
//        assertFalse(spec.validate(new Rec("123")).isValid());
//        assertFalse(spec.validate(new Rec("")).isValid());
//        assertEquals(
//                List.of(Valid.Violation.of("strValue", "Field [strValue] is expected to be null")),
//                spec.validate(new Rec("123")).violations()
//        );
//    }
//
//    @Test
//    void isNotNull__should_correctly_validate_notnull() {
//        record Rec(String strValue) {
//        }
//        var spec = Valid.<Rec>validationSpec(chain -> chain.isNotNull(Rec::strValue, "strValue"));
//
//        assertFalse(spec.validate(new Rec(null)).isValid());
//        assertFalse(spec.validate(new Rec("123")).isNotValid());
//        assertTrue(spec.validate(new Rec("123")).isValid());
//        assertTrue(spec.validate(new Rec("")).isValid());
//        assertEquals(
//                List.of(Valid.Violation.of("strValue", "Field [strValue] is expected to be non-null")),
//                spec.validate(new Rec(null)).violations()
//        );
//    }
//
//    @Test
//    void stringField__hasLength_and_not_blank__should_correctly_record_both_violations() {
//        record Rec(String strValue) {
//        }
//        var spec = Valid.<Rec>validationSpec(chain -> chain
//                .stringField(Rec::strValue, "strValue1").hasLength(5).rejectingNull()
//                .stringField(Rec::strValue, "strValue2").isNotBlank().rejectingNull()
//        );
//
//        assertEquals(
//                List.of(
//                        Valid.Violation.of("strValue1", "Field [strValue1] must be not null and have length [5]"),
//                        Valid.Violation.of("strValue2", "Field [strValue2] must be not null and non-blank string")
//                ),
//                spec.validate(new Rec(" ".repeat(4))).violations());
//    }
//
//    @Nested
//    class ViolationTest {
//
//        @Test
//        void equals_should_work_correctly() {
//            var violation1 = Valid.Violation.of("field", "message");
//            var violation2 = Valid.Violation.of("field", "message");
//            var violation3 = Valid.Violation.of("other", "message");
//            var violation4 = Valid.Violation.of("field", "other");
//
//            assertEquals(violation1, violation2);
//            assertNotEquals(violation1, violation3);
//            assertNotEquals(violation1, violation4);
//            assertNotEquals(null, violation1);
//            assertNotEquals("different type", violation1);
//        }
//
//        @Test
//        void hashCode_should_be_consistent() {
//            var violation1 = Valid.Violation.of("field", "message");
//            var violation2 = Valid.Violation.of("field", "message");
//
//            assertEquals(violation1.hashCode(), violation2.hashCode());
//        }
//
//        @Test
//        void toString_should_have_correct_format() {
//            var violation = Valid.Violation.of("testField", "test message");
//
//            assertEquals("Violation[field='testField', message='test message']", violation.toString());
//        }
//    }
//
//
//    @Test
//    void integerField__isInRange__should_correctly_validate_range() {
//        record Rec(Integer intValue) {
//        }
//        var function = Valid.<Rec>validationSpec(chain -> chain
//                .integerField(Rec::intValue, "intValue").isInRange(1, 10).rejectingNull()
//        ).toValidationFunction(() -> new RuntimeException("Fail"));
//
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(0)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(11)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(null)));
//        assertEquals(5, function.apply(new Rec(5)).intValue());
//        assertEquals(1, function.apply(new Rec(1)).intValue());
//        assertEquals(10, function.apply(new Rec(10)).intValue());
//    }
//
//    @Test
//    void integerField__isInRange__allowingNull_should_correctly_validate_range() {
//        record Rec(Integer intValue) {
//        }
//        var function = Valid.<Rec>validationSpec(chain -> chain
//                .integerField(Rec::intValue, "intValue").isInRange(1, 10).allowingNull()
//        ).toValidationFunction(() -> new RuntimeException("Fail"));
//
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(0)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(11)));
//        assertEquals(5, function.apply(new Rec(5)).intValue());
//        assertEquals(1, function.apply(new Rec(1)).intValue());
//        assertEquals(10, function.apply(new Rec(10)).intValue());
//        assertNull(function.apply(new Rec(null)).intValue());
//    }
//
//    @Test
//    void integerField__isGte__should_correctly_validate_gte() {
//        record Rec(Integer intValue) {
//        }
//        var function = Valid.<Rec>validationSpec(chain -> chain
//                .integerField(Rec::intValue, "intValue").isGte(5).rejectingNull()
//        ).toValidationFunction(() -> new RuntimeException("Fail"));
//
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(-1)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(0)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(4)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(null)));
//        assertEquals(5, function.apply(new Rec(5)).intValue());
//        assertEquals(10, function.apply(new Rec(10)).intValue());
//    }
//
//    @Test
//    void integerField__isLte__should_correctly_validate_lte() {
//        record Rec(Integer intValue) {
//        }
//        var function = Valid.<Rec>validationSpec(chain -> chain
//                .integerField(Rec::intValue, "intValue").isLte(5).rejectingNull()
//        ).toValidationFunction(() -> new RuntimeException("Fail"));
//
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(6)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(50)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(Integer.MAX_VALUE)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(null)));
//        assertEquals(5, function.apply(new Rec(5)).intValue());
//        assertEquals(0, function.apply(new Rec(0)).intValue());
//        assertEquals(-10, function.apply(new Rec(-10)).intValue());
//    }
//
//    @Test
//    void integerField__isInRange__failFast_if_ofInvalid_range_args() {
//        var actual = assertThrows(IllegalArgumentException.class, () -> {
//            Valid.<String>validationSpec(chain -> chain
//                    .integerField(String::length, "length").isInRange(2, 1).rejectingNull()
//            );
//        });
//
//        assertTrue(actual.getMessage().contains("minTarget must be less than maxTarget"));
//    }
//
//    @Nested
//    class orElseThrow_without_param {
//
//        @Test
//        void when_valid_then_no_exception() {
//            var expected = "val";
//            var valid = Valid.ofValid(expected);
//
//            assertEquals(expected, valid.orElseThrow(() -> new RuntimeException()));
//        }
//
//        @Test
//        void when_invalid_then_exception_thrown() {
//            var valid = Valid.ofInvalid(List.of(Valid.Violation.of("any")));
//            var expected = new RuntimeException("exception");
//
//            var actual = assertThrows(RuntimeException.class, () -> valid.orElseThrow(() -> expected));
//            assertSame(expected, actual);
//        }
//
//        @Test
//        void when_invalid_map_then_exception_thrown() {
//            var valid = Valid.<String>ofInvalid(List.of(Valid.Violation.of("any")))
//                    .map(String::toLowerCase);
//            var expected = new RuntimeException("exception");
//
//
//            var actual = assertThrows(RuntimeException.class, () -> valid.orElseThrow(() -> expected));
//            assertSame(expected, actual);
//        }
//
//        @Test
//        void when_invalid_flatmap_to_valid_then_exception_thrown() {
//            var valid = Valid.<String>ofInvalid(List.of(Valid.Violation.of("any")))
//                    .flatMap(str -> Valid.ofValid(str.toLowerCase()));
//            var expected = new RuntimeException("exception");
//
//
//            var actual = assertThrows(RuntimeException.class, () -> valid.orElseThrow(() -> expected));
//            assertSame(expected, actual);
//        }
//
//        @Test
//        void when_valid_flatmap_to_invalid_then_exception_thrown() {
//            var valid = Valid.ofValid("Value")
//                    .flatMap(str -> Valid.ofInvalid(List.of(Valid.Violation.of("any"))));
//            var expected = new RuntimeException("exception");
//
//
//            var actual = assertThrows(RuntimeException.class, () -> valid.orElseThrow(() -> expected));
//            assertSame(expected, actual);
//        }
//
//        @Test
//        void when_valid_flatmap_to_valid_then_no_exception_thrown() {
//            var expected = "New value";
//            var valid = Valid.ofValid("Value")
//                    .flatMap(str -> Valid.ofValid(expected));
//
//
//            assertSame(expected, valid.orElseThrow(() -> new RuntimeException()));
//        }
//    }
//
//    @Nested
//    class orElseThrow_with_violations_param {
//
//        @Test
//        void when_valid_then_no_exception() {
//            var expected = "val";
//            var valid = Valid.ofValid(expected);
//
//            assertSame(expected, valid.orElseThrow(violations -> new RuntimeException()));
//        }
//
//        @Test
//        void when_valid_supplier_then_no_exception() {
//            var expected = "val";
//            var valid = Valid.ofValid(() -> expected);
//
//            assertSame(expected, valid.orElseThrow(violations -> new RuntimeException()));
//        }
//
//        @Test
//        void when_invalid_then_exception_thrown() {
//            var expectedViolations = List.of(Valid.Violation.of("any"));
//            var valid = Valid.ofInvalid(expectedViolations);
//            var expected = new RuntimeException("exception");
//            var violationsRef = new AtomicReference<List<Valid.Violation>>();
//
//            var actual = assertThrows(RuntimeException.class, () -> valid.orElseThrow(violations -> {
//                violationsRef.set(violations);
//                return expected;
//            }));
//            assertSame(expected, actual);
//            assertEquals(expectedViolations, violationsRef.get());
//        }
//
//        @Test
//        void when_invalid_map_then_exception_thrown() {
//            var valid = Valid.<String>ofInvalid(List.of(Valid.Violation.of("any")))
//                    .map(String::toLowerCase);
//            var expected = new RuntimeException("exception");
//
//
//            var actual = assertThrows(RuntimeException.class, () -> valid.orElseThrow(() -> expected));
//            assertSame(expected, actual);
//        }
//
//        @Test
//        void when_invalid_flatmap_to_valid_then_exception_thrown() {
//            var valid = Valid.<String>ofInvalid(List.of(Valid.Violation.of("any")))
//                    .flatMap(str -> Valid.ofValid(str.toLowerCase()));
//            var expected = new RuntimeException("exception");
//
//
//            var actual = assertThrows(RuntimeException.class, () -> valid.orElseThrow(() -> expected));
//            assertSame(expected, actual);
//        }
//
//        @Test
//        void when_valid_flatmap_to_invalid_then_exception_thrown() {
//            var valid = Valid.ofValid("Value")
//                    .flatMap(str -> Valid.ofInvalid(List.of(Valid.Violation.of("any"))));
//            var expected = new RuntimeException("exception");
//
//
//            var actual = assertThrows(RuntimeException.class, () -> valid.orElseThrow(() -> expected));
//            assertSame(expected, actual);
//        }
//
//        @Test
//        void when_valid_flatmap_to_valid_then_no_exception_thrown() {
//            var expected = "New value";
//            var valid = Valid.ofValid("Value")
//                    .flatMap(str -> Valid.ofValid(expected));
//
//            assertSame(expected, valid.orElseThrow(() -> new RuntimeException()));
//        }
//    }
//
//    @Test
//    void alwaysValid__optional__should_return_value_optional() {
//        var valid = Valid.ofValid("Value");
//        assertEquals(Optional.of("Value"), valid.optional());
//    }
//
//    @Test
//    void invalid__optional__should_return_empty_optional() {
//        var invalid = Valid.ofInvalid(List.of(Valid.Violation.of("field", "error")));
//        assertEquals(Optional.empty(), invalid.optional());
//    }
//
//    @Test
//    void integerField__isInRange__should_include_boundaries_properly() {
//        record Rec(Integer value) {
//        }
//        var spec = Valid.<Rec>validationSpec(chain ->
//                chain.integerField(Rec::value, "value").isInRange(2, 5).rejectingNull()
//        );
//
//        assertTrue(spec.validate(new Rec(2)).isValid());
//        assertTrue(spec.validate(new Rec(5)).isValid());
//        assertFalse(spec.validate(new Rec(1)).isValid());
//        assertFalse(spec.validate(new Rec(6)).isValid());
//    }
//
//    @Test
//    void stringField__matches__should_correctly_validate_pattern_matching() {
//        record Rec(String strValue) {
//        }
//
//        var pattern = Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$");
//        var spec = Valid.<Rec>validationSpec(chain ->
//                chain.stringField(Rec::strValue, "strValue").matches(pattern).rejectingNull()
//        );
//
//        assertTrue(spec.validate(new Rec("123-45-6789")).isValid());
//        assertFalse(spec.validate(new Rec("123456789")).isValid());
//        assertFalse(spec.validate(new Rec("abc-def-ghi")).isValid());
//    }
//
//    @Test
//    void stringField__matches__should_throw_with_rejecting_null() {
//        record Rec(String strValue) {
//        }
//
//        var pattern = Pattern.compile("^[a-z]+@[a-z]+\\.[a-z]+$");
//        var spec = Valid.<Rec>validationSpec(chain ->
//                chain.stringField(Rec::strValue, "email").matches(pattern).rejectingNull()
//        );
//
//        assertThrows(RuntimeException.class, () -> spec.validate(new Rec(null)).orElseThrow(() -> new RuntimeException("Invalid")));
//        assertEquals("johndoe@gmail.com", spec.validate(new Rec("johndoe@gmail.com")).orElseThrow(() -> new RuntimeException("Invalid")).strValue());
//    }
//
//    @Test
//    void stringField__hasLengthRange__should_validate_strings() {
//        record Rec(String field) {
//        }
//        var spec = Valid.<Rec>validationSpec(chain ->
//                chain.stringField(Rec::field, "field").hasLengthRange(3, 6).rejectingNull()
//        );
//
//        assertTrue(spec.validate(new Rec("abc")).isValid());
//        assertTrue(spec.validate(new Rec("abcdef")).isValid());
//        assertFalse(spec.validate(new Rec("ab")).isValid());
//        assertFalse(spec.validate(new Rec("abcdefg")).isValid());
//    }
//
//    @Test
//    void stringField__hasMinLength__should_correctly_validate_min_length() {
//        record Rec(String strValue) {
//        }
//
//        var spec = Valid.<Rec>validationSpec(chain ->
//                chain.stringField(Rec::strValue, "strValue").hasMinLength(3).rejectingNull()
//        );
//
//        assertTrue(spec.validate(new Rec("abc")).isValid());
//        assertTrue(spec.validate(new Rec("abcd")).isValid());
//        assertFalse(spec.validate(new Rec("ab")).isValid());
//        assertFalse(spec.validate(new Rec("")).isValid());
//        assertFalse(spec.validate(new Rec(null)).isValid());
//    }
//
//    @Test
//    void longField__isInRange__should_correctly_validate_range() {
//        record Rec(Long longValue) {
//        }
//        var function = Valid.<Rec>validationSpec(chain -> chain
//                .longField(Rec::longValue, "longValue").isInRange(1L, 10L).rejectingNull()
//        ).toValidationFunction(() -> new RuntimeException("Fail"));
//
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(0L)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(11L)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(null)));
//        assertEquals(5L, function.apply(new Rec(5L)).longValue());
//        assertEquals(1L, function.apply(new Rec(1L)).longValue());
//        assertEquals(10L, function.apply(new Rec(10L)).longValue());
//    }
//
//    @Test
//    void doubleField__isInRange__should_correctly_validate_range() {
//        record Rec(Double doubleValue) {
//        }
//        var function = Valid.<Rec>validationSpec(chain -> chain
//                .doubleField(Rec::doubleValue, "doubleValue").isInRange(1.0, 10.0).rejectingNull()
//        ).toValidationFunction(() -> new RuntimeException("Fail"));
//
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(0.9)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(10.1)));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(null)));
//        assertEquals(5.0, function.apply(new Rec(5.0)).doubleValue());
//        assertEquals(1.0, function.apply(new Rec(1.0)).doubleValue());
//        assertEquals(10.0, function.apply(new Rec(10.0)).doubleValue());
//    }
//
//    @Test
//    void decimalField__isInRange__should_correctly_validate_range() {
//        record Rec(BigDecimal decimalValue) {
//        }
//        var function = Valid.<Rec>validationSpec(chain -> chain
//                .decimalField(Rec::decimalValue, "decimalValue")
//                .isInRange(new BigDecimal("1.0"), new BigDecimal("10.0"))
//                .rejectingNull()
//        ).toValidationFunction(() -> new RuntimeException("Fail"));
//
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(new BigDecimal("0.9"))));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(new BigDecimal("10.1"))));
//        assertThrows(RuntimeException.class, () -> function.apply(new Rec(null)));
//        assertEquals(0, function.apply(new Rec(new BigDecimal("5.0"))).decimalValue().compareTo(new BigDecimal("5.0")));
//        assertEquals(0, function.apply(new Rec(new BigDecimal("1.0"))).decimalValue().compareTo(new BigDecimal("1.0")));
//        assertEquals(0, function.apply(new Rec(new BigDecimal("10.0"))).decimalValue().compareTo(new BigDecimal("10.0")));
//    }
//}
