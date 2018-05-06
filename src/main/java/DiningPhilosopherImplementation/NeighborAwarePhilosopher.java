package DiningPhilosopherImplementation;

public class NeighborAwarePhilosopher extends Philosopher {
    private Philosopher leftNeighbor;
    private Philosopher rightNeighbor;
    private boolean isHungry;


    public NeighborAwarePhilosopher(int id, Chopstick leftChopstick, Chopstick rightChopstick,
                                    Philosopher leftNeighbor, Philosopher rightNeighbor) {
        super(id, leftChopstick, rightChopstick);
        this.leftNeighbor = leftNeighbor;
        this.rightNeighbor = rightNeighbor;
    }

    //if this philosopher has acquired at least one chopstick.
    protected synchronized boolean isHungry() {
        return isHungry;
    }

    public void setLeftNeighbor(Philosopher leftNeighbor) {
        this.leftNeighbor = leftNeighbor;
    }

    public void setRightNeighbor(Philosopher rightNeighbor) {
        this.rightNeighbor = rightNeighbor;
    }


    @Override
    public void pickupChopsticks() throws InterruptedException {
        this.philosopherState = tState.REQUESTING_CHOPSTICKS;
        boolean bothChopsticksAcquired = false;
        boolean areNeighborsHungry = false;

        while (!bothChopsticksAcquired) {
            synchronized (this) {
                areNeighborsHungry = this.areNeighborsHungry();
                if (!areNeighborsHungry) {
                    this.isHungry = true;           //declaring itself hungry, only when neighbours are not hungry.
                }
            }
            if(!areNeighborsHungry) {
                this.acquireChopstick(this.leftChopstick);
                this.acquireChopstick(this.rightChopstick);
                bothChopsticksAcquired = true;
            }else{
                if(((NeighborAwarePhilosopher)this.leftNeighbor).isHungry()) {
                    synchronized (this.leftNeighbor){
                        if(((NeighborAwarePhilosopher)this.leftNeighbor).isHungry()){
                            System.out.println("Philosopher " + this.getPhilospoherId() + " waiting for (left) Philosopher "
                            + this.leftNeighbor.getPhilospoherId() + " to get chopsticks and finish food, as it is hungry.");
                            this.leftNeighbor.wait();
                        }
                    }
                }
                if(((NeighborAwarePhilosopher)this.rightNeighbor).isHungry()) {
                    synchronized (this.rightNeighbor){
                        if(((NeighborAwarePhilosopher)this.rightNeighbor).isHungry()){
                            System.out.println("Philosopher " + this.getPhilospoherId() + " waiting for (right) Philosopher "
                                    + this.rightNeighbor.getPhilospoherId() + " to get chopsticks and finish food, as it is hungry.");
                            this.rightNeighbor.wait();
                        }
                    }
                }
            }
        }

        this.philosopherState = tState.EATING;
    }

    private synchronized boolean areNeighborsHungry() {
        return ((NeighborAwarePhilosopher) this.leftNeighbor).isHungry() || ((NeighborAwarePhilosopher) this.rightNeighbor).isHungry();
    }


    @Override
    public void releaseChopsticksAndStartThinkingAgain() {
        this.isHungry = false;
        super.releaseChopsticksAndStartThinkingAgain();
        //notifying on own hungry situation, which other neighboring philosopher depends upon.
        synchronized (this){
            this.notifyAll();
        }
    }

    @Override
    public void displayState() {
        super.displayState();

        System.out.println("Left Neighbour : " + this.leftNeighbor.getPhilospoherId());
        System.out.println("Right Neighbour : " + this.rightNeighbor.getPhilospoherId());
    }
}
