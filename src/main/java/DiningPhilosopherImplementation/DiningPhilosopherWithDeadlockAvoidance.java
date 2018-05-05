package DiningPhilosopherImplementation;

public class DiningPhilosopherWithDeadlockAvoidance extends DiningPhilosopher {

    private DeadlockAvoidanceStrategy deadlockAvoidanceStrategy;

    public DiningPhilosopherWithDeadlockAvoidance(DeadlockAvoidanceStrategy deadlockAvoidanceStrategy) {
        super();
        this.deadlockAvoidanceStrategy = deadlockAvoidanceStrategy;
    }

    @Override
    public void setupDiningPhilosopherProblem(){
        this.deadlockAvoidanceStrategy.setupDiningPhilosopherProblem(this);
    }
}
