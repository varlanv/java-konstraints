package com.varlanv.konstraints;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public interface Violation {

  String field();

  String message();

  Map<String, Object> context();

  static Violation of(String message) {
    return of("", message, Map.of());
  }

  static Violation of(String field, String message) {
    return of(field, message, Map.of());
  }

  static Violation of(String field, String message, Map<String, Object> context) {
    Objects.requireNonNull(field);
    Objects.requireNonNull(message);
    var ctx = Map.copyOf(context);
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
      public Map<String, Object> context() {
        return ctx;
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
