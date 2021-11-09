package com.example.servlet;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 模拟一个长时间的处理请求
 */
public class LongRunningProcess {

    public void run() {
        try {
            int millis = ThreadLocalRandom.current().nextInt(2000);
            String currentThread = Thread.currentThread().getName();
            System.out.println(currentThread + " sleep for " + millis + " milliseconds.");
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
