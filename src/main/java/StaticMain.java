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
    var fn = StaticValid.<Rec>validationSpec(rec -> rec
            .field("strValue")
            .assertNotNull(Rec::strValue)
            .field("nested1")
            .nullable()
            .nested(Rec::nested1, nested1 -> nested1
                .field("nested2")
                .nonNull()
                .nested(Nested1::nested2, nested2 -> nested2
                    .field("val2")
                    .nonNull()
                    .string(Nested2::val2, val2 -> val2
                        .assertLength(15))
                    .field("nestedListVals")
                    .nonNull()
                    .iterable()
                    .strings(Nested2::products)
                    .assertMaxSize(5)
                    .eachItem(assertions -> assertions
                        .assertLength(1))
                )
            )
        )
        .toValidationFunction();

    StaticValid<Rec> valid =
        fn.apply(
            new Rec(
                "test",
                1L,
                new Nested1(
                    "test",
                    new Nested2(
                        "test",
                        List.of("testA", "testB"),
                        Set.of(new NestedListVal1("testC", List.of(new NestedListVal2(7))))))));

    System.out.println(
        valid
            .map(Function.identity())
            .violations()
            .stream()
            .map(Object::toString)
            .collect(Collectors.joining("\n")));
  }
}
