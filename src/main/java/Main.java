import com.varlanv.konstraints.Valid;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public record NestedListVal(String val3) {
    }

    public record Nested2(String val2, List<String> products, Set<NestedListVal> nestedListVals) {
    }

    public record Nested1(String val1, Nested2 nested2) {
    }

    public record Rec(String strValue, Long longVal, Nested1 nested1) {
    }

    static final Function<Rec, Valid<Rec>> strValue = Valid.<Rec>validationSpec(rec -> rec
                    .field("strValue")
                    .assertNotNull(Rec::strValue)
                    .field("longVal")
                    .nullable()
                    .number(Rec::longVal, longVal -> longVal
                            .assertLte(1L))
                    .field("nested1")
                    .nullable()
                    .nested(Rec::nested1, nested1 -> nested1
                            .field("val1")
                            .nonNull()
                            .string(Nested1::val1, val1 -> val1
                                    .assertLength(15))
                            .field("nested2")
                            .nonNull()
                            .nested(Nested1::nested2, nested2 -> nested2
                                    .field("val2")
                                    .nonNull()
                                    .string(Nested2::val2, val2 -> val2
                                            .assertLength(1))
                                    .field("products")
                                    .nullable()
                                    .iterable()
                                    .strings(Nested2::products, products -> products
                                            .eachItem(product -> product
                                                    .assertLength(4)))
                            ))
            )
            .toValidationFunction();

    public static void main(String[] args) {
//        var strValue = Valid.<Rec>validationSpec(chain -> chain
//                        .isNotNull(Rec::strValue, "strValue")
//                        .extracting(Rec::nested1, "nested1")
//                        .extracting(Nested1::nested2, "nested2")
//                        .stringField(Nested2::val2, "val2").hasLength(15).rejectingNull()
//                        .customValidation(obj -> obj.val2().equals("123"), "Message")
//                )
//                .toValidationFunction();
//
        Valid<Rec> valid = strValue.apply(
                new Rec(
                        "test",
                        1L,
                        new Nested1(
                                "test",
                                new Nested2(
                                        "test",
                                        List.of(
                                                "testA",
                                                "testB"
                                        ),
                                        Set.of()
                                )
                        )
                )
        );

        System.out.println(valid.map(Function.identity()).violations().stream().map(Object::toString).collect(Collectors.joining("\n")));
    }
}
