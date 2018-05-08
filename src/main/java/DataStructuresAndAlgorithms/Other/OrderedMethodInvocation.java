package DataStructuresAndAlgorithms.Other;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderedMethodInvocation {
    private Semaphore firstComplete = new Semaphore(1);
    private Semaphore secondComplete = new Semaphore(1);

    public OrderedMethodInvocation(){
        try {
            firstComplete.acquire();
            secondComplete.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void firstMethod()  {
        System.out.println("Executed firstMethod()");
        firstComplete.release();
    }

    public void secondMethod() {
        try {
            firstComplete.acquire();
            firstComplete.release();

            System.out.println("Executed secondMethod()");
            secondComplete.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void thirdMethod() {

        try {
            secondComplete.acquire();
            secondComplete.release();

            System.out.println("Executed thirdMethod()");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
