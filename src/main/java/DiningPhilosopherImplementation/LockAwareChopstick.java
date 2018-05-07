package DiningPhilosopherImplementation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockAwareChopstick extends Chopstick {
    ReentrantLock chopstickLock = new ReentrantLock();

    public LockAwareChopstick(int i) {
        super(i);
    }

    public boolean acquireChopstickLock(Philosopher holder){
        return chopstickLock.tryLock();
    }

    public void releaseChopstickLock(){
        this.chopstickLock.unlock();
    }

}
