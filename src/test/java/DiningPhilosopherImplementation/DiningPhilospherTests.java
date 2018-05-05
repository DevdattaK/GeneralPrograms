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


    @BeforeEach
    void setUp() {
        obj = new DiningPhilosopher();
        obj.setupDiningPhilosopherProblem();
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

}
