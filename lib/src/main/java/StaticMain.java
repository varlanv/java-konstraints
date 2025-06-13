import com.varlanv.konstraints.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class StaticMain {

    //    static final Function<Rec, StaticValid<Rec>> strValue = StaticValid.<Rec>validationSpec(rec -> rec
    //                    .stringField())
    //            .toValidationFunction();

    private static void tst2() {
        Valid.<Root>validationSpec(rootSpec -> rootSpec.field("strValue")
                .nonNull()
                .string(Root::strValue, strValueSpec -> strValueSpec.length(1).check("Fail", String::isBlank))
                .field("longValue")
                .nullable()
                .number(Root::longVal, longValSpec -> longValSpec.inRange(1L, 3L))
                .field("nested1")
                .nonNull()
                .nested(Root::nested1, nested1Spec -> nested1Spec
                        .field("")
                        .nullable()
                        .nested(Nested1::nested2, nested2Spec -> nested2Spec
                                .field("val2")
                                .nonNull()
                                .string(Nested2::val2, val2Spec -> val2Spec.length(1)
                                        .check("Fail", String::isBlank)))));
    }

    private static void tst() {
        //    var fn = Valid.<Rec>validationSpec(rec -> rec
        //            .field("stringList")
        //            .nonNull()
        //            .iterable()
        //            .strings(Rec::stringList, stringList -> stringList
        //                .assertNotEmpty()
        //                .eachItem(item -> item
        //                    .assertCustom((a, b, c) -> c == 1)))
        //            .field("bigDecimalList")
        //            .nonNull()
        //            .iterable()
        //            .numbers(Rec::bigDecimalList, bigDecimalList -> bigDecimalList
        //                .assertNotEmpty()
        //                .eachItem(item -> item
        //                    .assertCustom((a, b, c) -> c == 0)))
        //            .field("nested1List")
        //            .nonNull()
        //            .iterable()
        //            .nested(Rec::nested1List, list -> list
        //                .assertNotEmpty()
        //                .eachItem(item -> item
        //                    .field("val4")
        //                    .nonNull()
        //                    .number(NestedListVal2::val4, val4 -> val4
        //                        .assertCustom((a, b) -> a > 0)
        //                        .assertCustom((a, b, c) -> c == 0))))
        //            .field("strValue")
        //            .assertNotNull(Rec::strValue)
        //            .field("strValue")
        //            .nonNull()
        //            .string(Rec::strValue, strVal -> strVal.assertLength(15))
        //            .field("longVal")
        //            .nullable()
        //            .number(Rec::longVal, longValSpec -> longValSpec
        //                .assertGte(1L))
        //            .field("nested1")
        //            .nullable()
        //            .nested(Rec::nested1, nested1 -> nested1
        //                .field("nested2")
        //                .nonNull()
        //                .nested(Nested1::nested2, nested2 -> nested2
        //                    .field("val2")
        //                    .nonNull()
        //                    .string(Nested2::val2, val2 -> val2
        //                        .assertCustom((a, b) -> {
        //                          Child<Nested2, Child<Nested1, Rec>> self = b;
        //                          Child<Nested1, Rec> parent1 = self.parent();
        //                          Rec parent = parent1.parent();
        //                          return true;
        //                        })
        //                        .assertLength(15))
        //                    .field("kek")
        //                    .nonNull()
        //                    .custom(Nested2::val2, val2 -> val2
        //                        .assertTrue((a, b) -> {
        //                          Child<Nested1, Rec> parent = b.parent();
        //                          Rec root = parent.parent();
        //                          return a.length() == 1;
        //                        }))
        //                    .field("nestedListVals")
        //                    .nonNull()
        //                    .iterable()
        //                    .nested(Nested2::nestedListVals, nestedListVals -> nestedListVals
        //                        .assertNotEmpty()
        //                        .eachItem(nestedListVal -> nestedListVal
        //                            .field("val3")
        //                            .nonNull()
        //                            .string(NestedListVal1::val3, val3 -> val3
        //                                .assertCustom((a, b, c) -> {
        //                                  return true;
        //                                }))
        //                            .field("nestedListVals2")
        //                            .nonNull()
        //                            .iterable()
        //                            .nested(NestedListVal1::nestedListVals2, nestedListVals2 -> nestedListVals2
        //                                .assertMaxSize(1)
        //                                .eachItem(nestedListVal2 -> nestedListVal2
        //                                    .field("val4")
        //                                    .nonNull()
        //                                    .number(NestedListVal2::val4, val4 -> val4
        //                                        .assertCustom((a, b, c) -> {
        //                                          Integer self = a;
        //                                          Child<NestedListVal2, NestedListVal1> parent1 = b.parent();
        //                                          NestedListVal1 parent2 = parent1.parent();
        //                                          return true;
        //                                        })
        //                                        .assertGte(5))))))))
        //        )
        //        .toValidationFunction();
        //
        //    Valid<Rec> valid =
        //        fn.apply(
        //            new Rec(
        //                "test",
        //                1L,
        //                new Nested1(
        //                    "test",
        //                    new Nested2(
        //                        "test",
        //                        List.of("testA", "testB"),
        //                        Set.of(
        //                            new NestedListVal1(
        //                                "testC",
        //                                List.of(new NestedListVal2(7))
        //                            )
        //                        )
        //                    )
        //                ),
        //                List.of(),
        //                List.of(),
        //                List.of()
        //            )
        //        );
        //
        //    System.out.println(
        //        valid
        //            .map(Function.identity())
        //            .violations()
        //            .stream()
        //            .map(Object::toString)
        //            .collect(Collectors.joining("\n")));
    }

    public static void main(String[] args) {}

    public class NestedListVal2 {

        private final Integer val4;

        public NestedListVal2(Integer val4) {
            this.val4 = val4;
        }

        public Integer val4() {
            return val4;
        }
    }

    public class NestedListVal1 {

        private final String val3;
        private final List<NestedListVal2> nestedListVals2;

        public NestedListVal1(String val3, List<NestedListVal2> nestedListVals2) {
            this.val3 = val3;
            this.nestedListVals2 = nestedListVals2;
        }

        public String val3() {
            return val3;
        }

        public List<NestedListVal2> nestedListVals2() {
            return nestedListVals2;
        }
    }

    public class Nested2 {

        private final String val2;
        private final List<String> products;
        private final Set<NestedListVal1> nestedListVals;

        public Nested2(String val2, List<String> products, Set<NestedListVal1> nestedListVals) {
            this.val2 = val2;
            this.products = products;
            this.nestedListVals = nestedListVals;
        }

        public String val2() {
            return val2;
        }

        public List<String> products() {
            return products;
        }

        public Set<NestedListVal1> nestedListVals() {
            return nestedListVals;
        }
    }

    public class Nested1 {

        private final String val1;
        private final Nested2 nested2;

        public Nested1(String val1, Nested2 nested2) {
            this.val1 = val1;
            this.nested2 = nested2;
        }

        public String val1() {
            return val1;
        }

        public Nested2 nested2() {
            return nested2;
        }
    }

    public class Root {

        private final String strValue;
        private final Long longVal;
        private final Nested1 nested1;
        private final List<NestedListVal2> nested1List;
        private final List<String> stringList;
        private final List<BigDecimal> bigDecimalList;

        public Root(
                String strValue,
                Long longVal,
                Nested1 nested1,
                List<NestedListVal2> nested1List,
                List<String> stringList,
                List<BigDecimal> bigDecimalList) {
            this.strValue = strValue;
            this.longVal = longVal;
            this.nested1 = nested1;
            this.nested1List = nested1List;
            this.stringList = stringList;
            this.bigDecimalList = bigDecimalList;
        }

        public String strValue() {
            return strValue;
        }

        public Long longVal() {
            return longVal;
        }

        public Nested1 nested1() {
            return nested1;
        }

        public List<NestedListVal2> nested1List() {
            return nested1List;
        }

        public List<String> stringList() {
            return stringList;
        }

        public List<BigDecimal> bigDecimalList() {
            return bigDecimalList;
        }
    }
}
