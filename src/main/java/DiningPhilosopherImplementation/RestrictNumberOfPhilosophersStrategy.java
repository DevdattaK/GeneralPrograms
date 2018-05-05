package DiningPhilosopherImplementation;

import java.util.stream.IntStream;

/*
Credit : https://www.cs.indiana.edu/classes/p415-sjoh/hw/project/dining-philosophers/index.htm
 */
public class RestrictNumberOfPhilosophersStrategy extends DeadlockAvoidanceStrategy{
    @Override
    public void setupDiningPhilosopherProblem(DiningPhilosopherWithDeadlockAvoidance diningPhilosopherWithDeadlockAvoidance) {
        IntStream.rangeClosed(0, diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - 1)
                .forEach(i -> diningPhilosopherWithDeadlockAvoidance.getChopsticks().add(i, new Chopstick(i)));

        final int chopstickCount = diningPhilosopherWithDeadlockAvoidance.getChopsticks().size();

        IntStream.rangeClosed(0, diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - 1)
                .limit(chopstickCount - 1)
                .forEach(i -> diningPhilosopherWithDeadlockAvoidance.getPhilosophers()
                        .add(new Philosopher(i,
                                diningPhilosopherWithDeadlockAvoidance.getChopsticks().get(i),
                                diningPhilosopherWithDeadlockAvoidance.getChopsticks().get((i + 1) % diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS()))));

        System.out.println("# of philosophers who were denied access to table : " + (diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - diningPhilosopherWithDeadlockAvoidance.getPhilosophers().size()));
    }
}
