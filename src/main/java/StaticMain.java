import com.varlanv.konstraints.StaticValid;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StaticMain {

    public record NestedListVal2(Integer val4) {
    }

    public record NestedListVal1(String val3, List<NestedListVal2> nestedListVals2) {
    }

    public record Nested2(String val2, List<String> products, Set<NestedListVal1> nestedListVals) {
    }

    public record Nested1(String val1, Nested2 nested2) {
    }

    public record Rec(String strValue, Long longVal, Nested1 nested1) {
    }

//    static final Function<Rec, StaticValid<Rec>> strValue = StaticValid.<Rec>validationSpec(rec -> rec
//                    .stringField())
//            .toValidationFunction();

    public static void main(String[] args) {
        var fn = StaticValid.<Rec>validationSpec(chain -> chain
                        .assertNotNull("strValue", Rec::strValue)
                        .nested("nested1", Rec::nested1)
                        .nested("nested2", Nested1::nested2)
                        .stringField("val2", Nested2::val2).assertLength(15).rejectNull()
                        .customValidation("Message", obj -> obj.val2().equals("123"))
                )
                .toValidationFunction();

        StaticValid<Rec> valid = fn.apply(
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
                                        Set.of(
                                                new NestedListVal1(
                                                        "testC",
                                                        List.of(
                                                                new NestedListVal2(7)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        System.out.println(valid.map(Function.identity()).violations().stream().map(Object::toString).collect(Collectors.joining("\n")));
    }
}
