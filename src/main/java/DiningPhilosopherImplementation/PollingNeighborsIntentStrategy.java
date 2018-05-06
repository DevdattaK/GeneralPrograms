package DiningPhilosopherImplementation;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class PollingNeighborsIntentStrategy extends DeadlockAvoidanceStrategy {

    private void updateNeighbours(DiningPhilosopherWithDeadlockAvoidance diningPhilosopherWithDeadlockAvoidance){
        List<Philosopher> philosophers =diningPhilosopherWithDeadlockAvoidance.getPhilosophers();
        final int leftNeighbourAdjusterOffset = diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - 1;

        for(int i = 0; i < diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS(); i++){
            NeighborAwarePhilosopher philosopher = (NeighborAwarePhilosopher)philosophers.get(i);
            philosopher.setLeftNeighbor(philosophers.get((i + leftNeighbourAdjusterOffset) % diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS()));
            philosopher.setRightNeighbor(diningPhilosopherWithDeadlockAvoidance.getPhilosophers().get((i + 1) % diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS()));
        }
    }

    @Override
    public void setupDiningPhilosopherProblem(DiningPhilosopherWithDeadlockAvoidance diningPhilosopherWithDeadlockAvoidance) {
        IntStream.rangeClosed(0, diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - 1)
                .forEach(i -> diningPhilosopherWithDeadlockAvoidance.getChopsticks().add(i, new Chopstick(i)));



        //add all lefty philosophers.
        IntStream.rangeClosed(0, diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS() - 1)
                .forEach(i -> diningPhilosopherWithDeadlockAvoidance.getPhilosophers()
                        .add(new NeighborAwarePhilosopher(i,
                                diningPhilosopherWithDeadlockAvoidance.getChopsticks().get(i),
                                diningPhilosopherWithDeadlockAvoidance.getChopsticks().get((i + 1) % diningPhilosopherWithDeadlockAvoidance.getNUMBER_OF_PHILOSOPHERS()),
                                null, null)));

        this.updateNeighbours(diningPhilosopherWithDeadlockAvoidance);
    }
}
