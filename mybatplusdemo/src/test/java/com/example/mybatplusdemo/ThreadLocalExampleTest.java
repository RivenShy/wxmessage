package com.example.mybatplusdemo;

import org.junit.jupiter.api.Test;

public class ThreadLocalExampleTest {

    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);


    @Test
    public void test() {
        Runnable task = () -> {
            int value = threadLocal.get();
            value += 1;
            threadLocal.set(value);
            System.out.println(Thread.currentThread().getName() + " Value: " + threadLocal.get());
        };

        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");

        thread1.start(); // 输出: Thread-1 Value: 1
        thread2.start(); // 输出: Thread-2 Value: 1
    }
}
