package com.example.mybatplusdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class Java8Test {

    @Test
    public void testLamda() {
        Runnable r1 = () -> System.out.println("Hello World 1");
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello World 2");
            }
        };
        process(r1);
        process(r2);
        process(() -> System.out.println("Hello World 3"));

    }
    public static void process(Runnable r) {
        r.run();
    }

    public static int strLength(String str) {
        return str.length();
    }

    @Test
    public void test212() {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 0);
        integers.forEach(i -> System.out.println(50/i));
    }

    @Test
    public void test215() {
        List<Object> listObject = new ArrayList<>();
        Stream<Object> streamObject = listObject.stream();

        String[] names = {"chaimm", "peter", "john", "john"};
        Stream<String> streamArr = Arrays.stream(names);

        Stream<String> streamString = Stream.of("chaimm", "peter", "john");

//        streamString.forEach(System.out::println);
//        Arrays.stream(names).forEach(System.out::println);
//        Arrays.stream(names).limit(2).forEach(System.out::println);
//        Arrays.stream(names).limit(2).forEach(System.out::println);
//        Arrays.stream(names).distinct().forEach(System.out::println);
//        Arrays.stream(names).sorted().forEach(System.out::println);
//        Arrays.stream(names).skip(1).forEach(System.out::println);
//        Object[] nameStreamToArr = Arrays.stream(names).toArray();
//        List listStreamToList = Arrays.stream(names).collect(Collectors.toList());
//        Set set = Arrays.stream(names).collect(Collectors.toSet());
//        Map map = Arrays.stream(names).collect(Collectors.toMap("1", x -> x));
//        System.out.println(Arrays.stream(names).count());
//        System.out.println(Arrays.stream(names).findFirst());
//        System.out.println(Arrays.stream(names).findAny());

        Stream.iterate(0, n -> n + 2).limit(10).forEach(System.out::println);
    }
}
