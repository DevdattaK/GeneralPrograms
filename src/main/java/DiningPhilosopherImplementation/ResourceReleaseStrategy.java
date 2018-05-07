package DiningPhilosopherImplementation;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.stream.IntStream;

public class ResourceReleaseStrategy extends DeadlockAvoidanceStrategy {

    @Override
    public void setupDiningPhilosopherProblem(DiningPhilosopherWithDeadlockAvoidance diningPhilosopherWithDeadlockAvoidance) {
        IntStream.rangeClosed(0, diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - 1)
                .forEach(i -> diningPhilosopherWithDeadlockAvoidance.getChopsticks().add(i, new LockAwareChopstick(i)));


        //add all lefty philosophers.
        IntStream.rangeClosed(0, diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - 1)
                .forEach(i -> diningPhilosopherWithDeadlockAvoidance.getPhilosophers()
                        .add(new ResourceSacrificingPhilosopher(i,
                                (LockAwareChopstick) diningPhilosopherWithDeadlockAvoidance.getChopsticks().get(i),
                                (LockAwareChopstick) diningPhilosopherWithDeadlockAvoidance.getChopsticks().get((i + 1) % diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS()))));

    }
}
