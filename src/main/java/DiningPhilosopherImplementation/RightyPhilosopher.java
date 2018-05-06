package DiningPhilosopherImplementation;

public class RightyPhilosopher extends Philosopher {
    public RightyPhilosopher(int rightyPhilosopherId, Chopstick leftChopstick, Chopstick rightChopstick) {
        super(rightyPhilosopherId, leftChopstick, rightChopstick);
    }


    //pick right chopstick first.
    @Override
    public void pickupChopsticks() throws InterruptedException {
        this.philosopherState = tState.REQUESTING_CHOPSTICKS;
        this.acquireChopstick(rightChopstick);
        this.acquireChopstick(leftChopstick);
        this.philosopherState = tState.EATING;
    }

}
