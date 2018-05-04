package dealockFreeLockManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceConsumingThread extends Thread {
    //threads which have acquired resources that this thread is asking for.
    private List<ResourceConsumingThread> dependencyList;
    private List<Resource> acquiredResources;
    private List<Resource> requestedResources;
    private volatile boolean isDeadlockDetected;
    private final String id;
    private static int threadIdGenerator;
    private Runnable runnableBody;

    public ResourceConsumingThread() {
        this.dependencyList = new ArrayList<>();
        this.acquiredResources = new ArrayList<>();
        this.requestedResources = new ArrayList<>();
        this.id = "T" + ++threadIdGenerator;
        this.runnableBody = null;
    }

    public ResourceConsumingThread(Runnable runnableBody) {
        this();
        this.runnableBody = runnableBody;
    }
    /*public ResourceConsumingThread getNewThread(Runnable runnableBody){
        ResourceConsumingThread newThread = new ResourceConsumingThread();
        newThread.runnableBody = runnableBody;
    }*/

    public List<ResourceConsumingThread> getDependencyList() {
        return dependencyList;
    }

    public List<Resource> getAcquiredResources() {
        return acquiredResources;
    }

    public List<Resource> getRequestedResources() {
        return requestedResources;
    }

    public boolean isDeadlockDetected() {
        return isDeadlockDetected;
    }

    @Override
    public void run() {
        System.out.println("Running body for thread : " + id);
        if(runnableBody != null){
           runnableBody.run();
        }else{
            System.out.println("ERROR...threadBody can't be null.");
        }
    }

    public void setDeadlockDetected(boolean deadlockDetected) {
        this.isDeadlockDetected = deadlockDetected;
    }

    public String getCustomId() {
        return id;
    }

    public void setRunnableBody(Runnable runnableBody){
        this.runnableBody = runnableBody;
    }

    @Override
    public String toString() {
        return " " + id;
    }

}
