package DiningPhilosopherImplementation;


/*
1. Lefty-Righty
2. Allowing only N-1 philosophers to access table if there are N chopsticks
3. Poll the intent of neighbours and see if they have acquired at least one chopstick. If they have, then don't acquire the chopsticks though available.
**/

public abstract class DeadlockAvoidanceStrategy {
    protected DiningPhilosopher diningPhilosopher;


    public abstract void setupDiningPhilosopherProblem(DiningPhilosopherWithDeadlockAvoidance diningPhilosopherWithDeadlockAvoidance);
}
