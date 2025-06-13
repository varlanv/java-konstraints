package com.varlanv.konstraints;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public interface Violation {

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
