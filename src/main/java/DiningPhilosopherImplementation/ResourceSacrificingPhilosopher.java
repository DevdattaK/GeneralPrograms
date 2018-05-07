package DiningPhilosopherImplementation;

public class ResourceSacrificingPhilosopher extends Philosopher {

    public ResourceSacrificingPhilosopher(int i, LockAwareChopstick leftChopstick, LockAwareChopstick rightChopstick) {
        super(i, leftChopstick, rightChopstick);
    }


    @Override
    protected void pickupChopsticks() throws InterruptedException {
        this.philosopherState = tState.REQUESTING_CHOPSTICKS;
        boolean acquiredBothChopsticks = false;

        while(!acquiredBothChopsticks) {
            //if Philosopher could only acquire lock for left chopstick, but not for right, then release left chopstick/lock
            if (((LockAwareChopstick) this.leftChopstick).acquireChopstickLock(this)) {
                if (!((LockAwareChopstick) this.rightChopstick).acquireChopstickLock(this)) {
                    /*System.out.println("Philosopher " + this.getPhilospoherId() +" is releasing lock for Chopstick "
                    + this.leftChopstick.getChopstickId() + " because it couldn't acquire lock for Chopstick"
                    + this.rightChopstick.getChopstickId());*/
                    ((LockAwareChopstick) this.leftChopstick).releaseChopstickLock();
                }else {
                    acquiredBothChopsticks = true;
                }
            }
        }

        this.philosopherState = tState.EATING;
    }

    @Override
    protected void releaseChopsticksAndStartThinkingAgain() {
        philosopherState = tState.RELEASING_CHOPSTICKS;

        ((LockAwareChopstick)this.leftChopstick).releaseChopstickLock();
        ((LockAwareChopstick)this.rightChopstick).releaseChopstickLock();

        philosopherState = tState.THINKING;
    }
}
