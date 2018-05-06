package DiningPhilosopherImplementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class DiningPhilospherTests {
    private DiningPhilosopher obj;
    private DiningPhilosopherWithDeadlockAvoidance deadlockFreePhilosophers_LeftyRightCombo;
    private DiningPhilosopherWithDeadlockAvoidance deadlockFreePhilosophers_RestrictedAccess;
    private DiningPhilosopherWithDeadlockAvoidance getDeadlockFreePhilosophers_PollingNeighbors;

    @BeforeEach
    void setUp() {
        obj = new DiningPhilosopher();
        obj.setupDiningPhilosopherProblem();

        deadlockFreePhilosophers_LeftyRightCombo = new DiningPhilosopherWithDeadlockAvoidance(new AtleastOneRightyPhilosopherStrategy());
        deadlockFreePhilosophers_LeftyRightCombo.setupDiningPhilosopherProblem();

        deadlockFreePhilosophers_RestrictedAccess = new DiningPhilosopherWithDeadlockAvoidance(new RestrictNumberOfPhilosophersStrategy());
        deadlockFreePhilosophers_RestrictedAccess.setupDiningPhilosopherProblem();

        getDeadlockFreePhilosophers_PollingNeighbors = new DiningPhilosopherWithDeadlockAvoidance(new PollingNeighborsIntentStrategy());
        getDeadlockFreePhilosophers_PollingNeighbors.setupDiningPhilosopherProblem();
    }

    @Test
    void problemSetupTest() {
        assertEquals(obj.getPhilosophers().size(), obj.getNUMBER_OF_PHILOSOPHERS());
        boolean isInitializedCorrectly = obj.getPhilosophers().stream()
                .noneMatch(p -> p.getPhilospoherId() < 1
                        && p.getPhilospoherId() > 5
                        && p.getPhilosopherState() != Philosopher.tState.THINKING);

        assertTrue(isInitializedCorrectly);
    }

    @Test
    void pickupChopstickSimpleTest() throws InterruptedException {
        int philosopherId = (new Random()).nextInt(5);
        Philosopher p = obj.getPhilosophers().get(philosopherId);

        assertFalse(p.getLeftChopstick().isInUse());
        assertFalse(p.getRightChopstick().isInUse());
        assertEquals(p.getPhilosopherState(), Philosopher.tState.THINKING);

        p.pickupChopsticks();

        assertTrue(p.getLeftChopstick().isInUse());
        assertTrue(p.getRightChopstick().isInUse());
        assertEquals(p.getPhilosopherState(), Philosopher.tState.EATING);
    }

    @Test
    void pickupLeftChopstickForEachPhilosopherCreatingDeadlockTest() throws InterruptedException {
        System.out.println("Allowing each philosopher to eat 5 times");

        ExecutorService service = Executors.newFixedThreadPool(obj.getNUMBER_OF_PHILOSOPHERS());

        for (Philosopher p :
                obj.getPhilosophers()) {
            service.submit(p);
        }

        service.shutdown();

        service.awaitTermination(1, TimeUnit.HOURS);
    }

    @Test
    void deadlockFreePhilosophers_LeftyRightyStrategy_Test() throws InterruptedException {
        System.out.println("Allowing each philosopher to eat 5 times");


        IntStream.rangeClosed(0, 5).forEach(i ->
        {
            System.out.println("\n ________________Execution# : " + (i+1) + "______________________________");
            ExecutorService service = Executors.newFixedThreadPool(deadlockFreePhilosophers_LeftyRightCombo.getNUMBER_OF_PHILOSOPHERS());

            for (Philosopher p :
                    deadlockFreePhilosophers_LeftyRightCombo.getPhilosophers()) {
                service.submit(p);
            }

            service.shutdown();

            try {
                service.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                deadlockFreePhilosophers_LeftyRightCombo.resetEatingCounter();
            }
        });
    }

    @Test
    void deadlockFreePhilosophers_RestrictedAccessStrategy_Test() {
        System.out.println("Allowing each philosopher to eat 5 times");


        IntStream.rangeClosed(0, 5).forEach(i ->
        {
            System.out.println("\n ________________Execution# : " + (i+1) + "______________________________");
            ExecutorService service = Executors.newFixedThreadPool(deadlockFreePhilosophers_RestrictedAccess.getPhilosophers().size());

            for (Philosopher p :
                    deadlockFreePhilosophers_RestrictedAccess.getPhilosophers()) {
                service.submit(p);
            }

            service.shutdown();

            try {
                service.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                deadlockFreePhilosophers_RestrictedAccess.resetEatingCounter();
            }
        });
    }

    @Test
    void neighbourAwareDiningSetup_test() {
        getDeadlockFreePhilosophers_PollingNeighbors.displayCurrentStateOfDiningTable();
    }

    @Test
    void neighbourHungryTest() throws InterruptedException {
        Philosopher p0 = getDeadlockFreePhilosophers_PollingNeighbors.getPhilosophers().get(0);
        Philosopher p1 = getDeadlockFreePhilosophers_PollingNeighbors.getPhilosophers().get(1);

        p0.start();
        p1.start();

        p0.join();
        p1.join();

    }

    @Test
    void deadlockFreePhilosophers_PollNeighborIntent_Test() {
        System.out.println("Allowing each philosopher to eat 5 times");


        IntStream.rangeClosed(0, 5).forEach(i ->
        {
            System.out.println("\n ________________Execution# : " + (i+1) + "______________________________");
            ExecutorService service = Executors.newFixedThreadPool(getDeadlockFreePhilosophers_PollingNeighbors.getNUMBER_OF_PHILOSOPHERS());

            for (Philosopher p :
                    getDeadlockFreePhilosophers_PollingNeighbors.getPhilosophers()) {
                service.submit(p);
            }

            service.shutdown();

            try {
                service.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                getDeadlockFreePhilosophers_PollingNeighbors.resetEatingCounter();
            }
        });
    }
}
