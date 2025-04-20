import com.varlanv.konstraints.Valid;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Main {

    public record NestedListVal(String val3) {
    }

    public record Nested2(String val2, List<String> list, Set<NestedListVal> nestedListVals) {
    }

    public record Nested1(String val1, Nested2 nested2) {
    }

    public record Rec(String strValue, Nested1 nested1) {
    }

    static final Function<Rec, Valid<Rec>> strValue = Valid.<Rec>validationSpec((rec, recChain) -> recChain
                    .nullable().nestedField(Rec::nested1, "nested1", (nested1, nested1Chain) -> nested1Chain
                            .nonNull().stringField(Nested1::val1, "val1").assertLength(15)
                            .nonNull().nestedField(Nested1::nested2, "nested2", (nested2, nested2Chain) -> nested2Chain
                                    .nonNull().stringField(Nested2::val2, "val2").assertLength(1)
                                    .nonNull().iterableField().strings(Nested2::list, "list", listSpec -> listSpec
                                            .eachItem(s -> s.assertEmpty()))
                                    .nonNull().iterableField().
                            )
                    )
                    .assertNotNull(Rec::strValue, "strValue"))
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
//        Valid<Rec> valid = strValue.apply(new Rec("test", new Nested1("test", new Nested2("test"))));
//
//        System.out.println(valid.map(Function.identity()).violations());

//        Valid<Rec> valid = strValue.apply(new Rec("test1", new Nested1("test2", new Nested2("test3"))));
//
//        System.out.println(valid.map(Function.identity()).violations());
    }
}
