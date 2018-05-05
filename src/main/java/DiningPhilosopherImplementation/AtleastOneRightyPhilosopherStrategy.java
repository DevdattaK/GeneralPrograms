package DiningPhilosopherImplementation;

import java.util.Random;
import java.util.stream.IntStream;

/*
Credit : https://pages.mtu.edu/~shene/NSF-3/e-Book/MUTEX/TM-example-left-right.html
 */

public class AtleastOneRightyPhilosopherStrategy extends DeadlockAvoidanceStrategy {

    @Override
    public void setupDiningPhilosopherProblem(DiningPhilosopherWithDeadlockAvoidance diningPhilosopherWithDeadlockAvoidance) {
        IntStream.rangeClosed(0, diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - 1)
                .forEach(i -> diningPhilosopherWithDeadlockAvoidance.getChopsticks().add(i, new Chopstick(i)));

        Random random = new Random();
        int rightyPhilosopherId = random.nextInt(5);

        //add all lefty philosophers.
        IntStream.rangeClosed(0, diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - 1)
                .filter(i -> i != rightyPhilosopherId)
                .forEach(i -> diningPhilosopherWithDeadlockAvoidance.getPhilosophers()
                        .add(new Philosopher(i,
                                diningPhilosopherWithDeadlockAvoidance.getChopsticks().get(i),
                                diningPhilosopherWithDeadlockAvoidance.getChopsticks().get((i + 1) % diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS()))));


        //add the righty philosopher skipped from above loop
        diningPhilosopherWithDeadlockAvoidance.getPhilosophers()
                            .add(new RightyPhilosopher(rightyPhilosopherId,
                                    diningPhilosopherWithDeadlockAvoidance.getChopsticks().get(rightyPhilosopherId),
                                    diningPhilosopherWithDeadlockAvoidance.getChopsticks().get((rightyPhilosopherId+1) % diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS())));
    }
}
