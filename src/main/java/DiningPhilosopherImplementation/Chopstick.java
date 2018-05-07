package DiningPhilosopherImplementation;

public class Chopstick {
    private final int chopstickId;
    public enum tStatus {IN_USE, AVAILABLE};
    private volatile tStatus status;
    protected volatile Philosopher chopstickHolder;

    public Chopstick(int id) {
        chopstickId = id;
        status = tStatus.AVAILABLE;
    }

    public int getChopstickId() {
        return chopstickId;
    }

    public synchronized tStatus getStatus() {
        return status;
    }

    public synchronized boolean isInUse(){
        return status == tStatus.IN_USE;
    }

    public synchronized String getCurrentState(){
        return "Id : " + chopstickId + ", status : " + status;
    }

    private synchronized void setStatus(tStatus status) {
        this.status = status;
    }

    public synchronized void acquire(Philosopher byPhilosopher){
        if(!this.isInUse()) {
            this.setStatus(tStatus.IN_USE);
            this.chopstickHolder = byPhilosopher;
        }
    }

    public synchronized Philosopher getChopstickHolder(){
        return chopstickHolder;
    }

    public synchronized void release(){
        this.status = tStatus.AVAILABLE;
        this.chopstickHolder = null;
    }
}
