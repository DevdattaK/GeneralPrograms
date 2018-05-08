package DataStructureAndAlgorithmTest.Other;

import DataStructuresAndAlgorithms.Other.OrderedMethodInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MethodOrderExecTest {
    private OrderedMethodInvocation obj;

    @BeforeEach
    void setUp() {
        obj = new OrderedMethodInvocation();
    }

    @Test
    void orderedMethodTest() throws InterruptedException {
        Thread t1 = new Thread(()-> obj.firstMethod());

        Thread t2 = new Thread(()->
            obj.secondMethod()
        );

        Thread t3 = new Thread(()-> obj.thirdMethod());

        t3.start();
        t2.start();
        t1.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Finished.");
    }
}
