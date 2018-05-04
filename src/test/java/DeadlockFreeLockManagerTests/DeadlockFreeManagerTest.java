package DeadlockFreeLockManagerTests;

import dealockFreeLockManager.DeadlockFreeLockManager;
import dealockFreeLockManager.Resource;
import dealockFreeLockManager.ResourceConsumingThread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeadlockFreeManagerTest {
    private DeadlockFreeLockManager lockManager;


    @BeforeEach
    public void setUp() throws Exception {
        lockManager = DeadlockFreeLockManager.getInstance();
    }

    @Test
    public void singletonTestForLockManager() {
        DeadlockFreeLockManager lockManager1 = DeadlockFreeLockManager.getInstance();
        assertSame(lockManager, lockManager1);
    }

    @Test
    public void threadCreationTest() throws InterruptedException{
        final ResourceConsumingThread t1 = new ResourceConsumingThread();
        Runnable t1Runnable = new Runnable() {
            @Override
            public void run() {
                lockManager.acquireLockOnResourceForThread(new Resource(), t1);
            }
        };
        lockManager.initThreadWithRunnableBody(t1Runnable, t1);

        t1.start();
        t1.join();

        System.out.println("Completed.");
    }

    @Test
    public void acquireLockTest() throws InterruptedException{
        final Map<String, Resource> resourceMap = new HashMap<>();
        resourceMap.put("R1", new Resource());

        final ResourceConsumingThread t1 = new ResourceConsumingThread();
        Runnable t1Runnable = new Runnable() {
            @Override
            public void run() {
                lockManager.acquireLockOnResourceForThread(resourceMap.get("R1"), t1);
            }
        };
        t1.setRunnableBody(t1Runnable);
        t1.start();
        t1.join();

        System.out.println("Locks acquired by " + t1.getCustomId() + " for resources => " +
                t1.getAcquiredResources().stream().map(r -> r.getId()).collect(Collectors.joining(",")));

    }

    @Test
    public void acquireLockWithMethod_createNewThread_Test() throws InterruptedException{
       ResourceConsumingThread t = lockManager.createNewThread(() -> {

           lockManager.acquireLockOnResourceForThread(new Resource(), (ResourceConsumingThread) Thread.currentThread());
       });

       t.start();
       t.join();

       lockManager.displayThreadRegistry();
    }

    @Test
    boolean threadDependencyListTest() throws InterruptedException{
        final Map<String, Resource> resourceMap = new HashMap<>();
        resourceMap.put("R1", new Resource());
        resourceMap.put("R2", new Resource());
        resourceMap.put("R3", new Resource());


        //thread-1
        final ResourceConsumingThread t1 = new ResourceConsumingThread();
        Runnable t1Runnable = () -> {
                try {
                    lockManager.acquireLockOnResourceForThread(resourceMap.get("R1"), t1);
                    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));

                    //request additional lock. By this time, remaining threads should have acquired locks on their assigned resources.
                    lockManager.acquireLockOnResourceForThread(resourceMap.get("R2"), t1);
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            };
        lockManager.initThreadWithRunnableBody(t1Runnable, t1);

        //thread-2
        final ResourceConsumingThread t2 = new ResourceConsumingThread();
        Runnable t2Runnable = () -> {
                try {
                    lockManager.acquireLockOnResourceForThread(resourceMap.get("R2"), t2);
                    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));

                    //request additional lock. By this time, remaining threads should have acquired locks on their assigned resources.
                    lockManager.acquireLockOnResourceForThread(resourceMap.get("R3"), t2);
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            };
        lockManager.initThreadWithRunnableBody(t2Runnable, t2);

        //thread-3
        final ResourceConsumingThread t3 = new ResourceConsumingThread();
        Runnable t3Runnable = () -> {
            try {
                lockManager.acquireLockOnResourceForThread(resourceMap.get("R3"), t3);
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));

                //request additional lock. By this time, remaining threads should have acquired locks on their assigned resources.
                lockManager.acquireLockOnResourceForThread(resourceMap.get("R1"), t3);
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        };
        lockManager.initThreadWithRunnableBody(t3Runnable, t3);


        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

       // lockManager.displayThreadRegistry();
        System.out.println("IsDeadlock detected : " + t1.isDeadlockDetected());
        return ResourceConsumingThread.isDeadlockDetected();
    }


    @Test
    void exhaustiveDeadlockDetection() throws Exception{

        IntStream.range(0, 5)
                 .forEach(i -> {
                     try {
                         boolean deadlockDetected = threadDependencyListTest();
                         assertTrue(deadlockDetected);
                         lockManager.cleanThreadRegistry();
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 });
    }
}
