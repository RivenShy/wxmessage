//package com.example.mybatplusdemo.exception;
//
//import java.util.Objects;
//import java.util.function.Function;
//
//public class Try {
//    public static <T, R> Function<T, R> of(UncheckedFunction<T, R> mapper) {
//        Objects.requireNonNull(mapper);
//        return t -> {
//            try {
//                return mapper.apply(t);
//            } catch (Exception e) {
//                throw Exceptions.unchecked(e);
//            }
//        };
//    }
//    @FunctionalInterface
//    public interface UncheckedFunction<T, R> {
//        R apply(T t) throws Exception;
//    }
//}