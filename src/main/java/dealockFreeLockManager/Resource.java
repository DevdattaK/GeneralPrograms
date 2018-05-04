package dealockFreeLockManager;

public class Resource {
    private final String id;
    private static int idGenerator;
    private volatile boolean isLocked;
    private volatile ResourceConsumingThread lockedBy;

    public Resource(){
        this.id = "R" + ++idGenerator;
    }

    public boolean isLocked() {
        return isLocked;
    }

    private void setIsLocked(){
        isLocked = true;
    }

    public ResourceConsumingThread getLockedBy() {
        return lockedBy;
    }

    public synchronized void setLocked(ResourceConsumingThread byThread){
        this.setIsLocked();
        lockedBy = byThread;
    }

    public String getId() {
        return id;
    }
}
