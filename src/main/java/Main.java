import com.varlanv.konstraints.Valid;

import java.util.function.Function;

public class Main {

    public record Nested2(String val2) {
    }

    public record Nested1(String val1, Nested2 nested2) {
    }

    public record Rec(String strValue, Nested1 nested1) {
    }

    static final Function<Rec, Valid<Rec>> strValue = Valid.<Rec>validationSpecV2((rec, recChain) -> recChain
                    .extractingNullable(Rec::nested1, "nested1", (nested1, nested1Chain) -> nested1Chain
                            .stringField(Nested1::val1, "val1").hasLength(15).rejectingNull()
                            .extractingNonNull(Nested1::nested2, "nested2", (nested2, nested2Chain) -> nested2Chain
                                    .stringField(Nested2::val2, "val2").hasLength(1).rejectingNull()))
                    .isNotNull(Rec::strValue, "strValue"))
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

        Valid<Rec> valid = strValue.apply(new Rec("test1", new Nested1("test2", new Nested2("test3"))));

        System.out.println(valid.map(Function.identity()).violations());
    }
}
