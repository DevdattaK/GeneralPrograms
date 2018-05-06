package DiningPhilosopherImplementation;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Philosopher extends Thread {
    private final int philospoherId;
    private int numberOfTimesEaten = 0;
    private static final int MAX_NUMBER_OF_EATING_ALLOWED = 4;
    public enum tState {EATING, THINKING, REQUESTING_CHOPSTICKS, RELEASING_CHOPSTICKS}
    protected tState philosopherState;
    protected final Chopstick leftChopstick;
    protected final Chopstick rightChopstick;


    public Philosopher(int id, Chopstick leftChopstick, Chopstick rightChopstick) {
        this.philospoherId = id;
        this.philosopherState = tState.THINKING;
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
    }

    public void resetEatingCounter(){
        numberOfTimesEaten = 0;
    }

    public void displayState() {
        System.out.println("Philosopher-" + philospoherId);
        System.out.println("Left chopstick status: " + leftChopstick.getCurrentState());
        System.out.println("Right chopstick status: " + rightChopstick.getCurrentState());
        System.out.println("State : " + philosopherState);
    }

    public int getPhilospoherId() {
        return philospoherId;
    }

    public tState getPhilosopherState() {
        return philosopherState;
    }

    public Chopstick getLeftChopstick() {
        return leftChopstick;
    }

    public Chopstick getRightChopstick() {
        return rightChopstick;
    }

    protected void acquireChopstick(Chopstick chopstick) throws InterruptedException {
        boolean isAcquiredByCurrentPhilosopher = false;

        while (!isAcquiredByCurrentPhilosopher) {
            synchronized (chopstick) {
                if (!chopstick.isInUse() && chopstick.getChopstickHolder() != this) {
                    chopstick.acquire(this);
                    isAcquiredByCurrentPhilosopher = true;
                    System.out.println("Philosopher " + philospoherId + " acquired chopstick " + chopstick.getChopstickId());
                } else {
                    System.out.println("Philosopher " + philospoherId +
                            " is waiting as chopstick " + chopstick.getChopstickId() + " is being used by Philosopher "
                            + chopstick.getChopstickHolder().getPhilospoherId());
                    chopstick.wait();
                }
            }

        }
    }

    protected void pickupChopsticks() throws InterruptedException {
        this.philosopherState = tState.REQUESTING_CHOPSTICKS;
        this.acquireChopstick(leftChopstick);
        this.acquireChopstick(rightChopstick);
        this.philosopherState = tState.EATING;
    }

    @Override
    public void run() {
        while (numberOfTimesEaten < MAX_NUMBER_OF_EATING_ALLOWED) {
            if (this.philosopherState == tState.THINKING) {
                try {
                    //waiting upto 3 secs randomly.
                    TimeUnit.SECONDS.sleep((new Random()).nextInt(3));

                    this.doEatSphagetti();

                    numberOfTimesEaten++;

                    this.releaseChopsticksAndStartThinkingAgain();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void releaseChopstick(Chopstick chopstick) {
        synchronized (chopstick) {
            chopstick.release();
            System.out.println("Philosopher " + philospoherId + " released chopstick " + chopstick.getChopstickId());
            chopstick.notifyAll();
        }
    }

    protected void releaseChopsticksAndStartThinkingAgain() {
        philosopherState = tState.RELEASING_CHOPSTICKS;

        this.releaseChopstick(leftChopstick);
        this.releaseChopstick(rightChopstick);

        philosopherState = tState.THINKING;
    }

    private void doEatSphagetti() throws InterruptedException {
        this.pickupChopsticks();
        System.out.println("Philosopher " + philospoherId + " acquired both chopsticks. Simulating eating time. \n \n ");
        //this.displayState();
        TimeUnit.SECONDS.sleep(5);
    }
}