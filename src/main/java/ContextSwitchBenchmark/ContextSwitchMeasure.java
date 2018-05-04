package ContextSwitchBenchmark;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ContextSwitchMeasure {
    private long startTimeInNanoSecs;
    private long endTimeInNanoSecs;

    public static void main(String[] args) throws InterruptedException{
        ContextSwitchMeasure obj = new ContextSwitchMeasure();

        Thread t1 = new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " Requesting lock on obj");
                    synchronized (obj){
                        TimeUnit.SECONDS.sleep(0);
                        obj.startTimeInNanoSecs = Instant.now().getNano();

                        System.out.println(Thread.currentThread().getName() + " Releasing lock on obj");
                        obj.wait();

                        obj.endTimeInNanoSecs = Instant.now().getNano();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        Thread t2 = new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(0);
                    synchronized (obj){
                        System.out.println(Thread.currentThread().getName() + " Acquired lock on obj");

                        obj.notifyAll();

                        System.out.println(Thread.currentThread().getName() + " Released lock on obj");

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        t1.start();

        Thread.sleep(2000);

        t2.start();

        t1.join();
        t2.join();

        System.out.println("ContextSwitch time : " + (obj.endTimeInNanoSecs - obj.startTimeInNanoSecs));
    }
}
