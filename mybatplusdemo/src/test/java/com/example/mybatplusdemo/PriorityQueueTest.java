package com.example.mybatplusdemo;

import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

public class PriorityQueueTest {

    @Test
    public void test(){
        PriorityQueue<Integer> pq = new PriorityQueue<>(
                (a,b)->b-a
        );
        pq.add(1);
        pq.add(2);
        pq.add(3);
        pq.add(4);
        pq.add(5);
        while (!pq.isEmpty()){
            System.out.println(pq.poll());
        }
    }
}
