package dealockFreeLockManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockFreeLockManager {
    private final int MAX_THREAD_CAPACITY = 100;
    private static DeadlockFreeLockManager instance;            //also try with volatile
    private static final ReentrantLock instanceLock = new ReentrantLock();
    private static List<ResourceConsumingThread> threadRegistry;

    private DeadlockFreeLockManager() {
        //trailOfTraversedThreads = new ArrayBlockingQueue<>(MAX_THREAD_CAPACITY);
        threadRegistry = new ArrayList<>();
    }

    public void initThreadWithRunnableBody(Runnable runnableBody, ResourceConsumingThread t) {
        t.setRunnableBody(runnableBody);
        threadRegistry.add(t);
    }

    public ResourceConsumingThread createNewThread(Runnable runnable){
        ResourceConsumingThread thread = new ResourceConsumingThread(runnable);
        threadRegistry.add(thread);

        return thread;
    }

    //to be invoked when a thread asks for new resource.
    private synchronized void acquireLockIfPossibleAndUpdateDependencies(Resource requestedResource, ResourceConsumingThread byThread) {
        if (requestedResource != null) {
            if (requestedResource.isLocked()) {
                System.out.println("Resource " + requestedResource.getId() + " is locked by : " + Thread.currentThread().getName());
                byThread.getDependencyList().add(requestedResource.getLockedBy());
                byThread.getRequestedResources().add(requestedResource);
            } else {
                byThread.getAcquiredResources().add(requestedResource);
                requestedResource.setLocked(byThread);
            }

            List<ResourceConsumingThread> threadsVisitedSoFar = new ArrayList<>();
            byThread.setDeadlockDetected(isDeadlockDetected(threadsVisitedSoFar, byThread, byThread));
        } else {
            System.out.println("Resource passed is null.");
        }
    }

    public void acquireLockOnResourceForThread(Resource requestedResource, ResourceConsumingThread thread) {
        if (!thread.isDeadlockDetected()) {
            this.acquireLockIfPossibleAndUpdateDependencies(requestedResource, thread);
        }else{
            thread.setDeadlockDetected(true);
            System.out.println("Deadlock detected on resource " + requestedResource.getId() + ". Lock can't be granted to thread ID : " + thread.getCustomId());
        }
    }


    public static DeadlockFreeLockManager getInstance() {
        if (instance == null) {
            instanceLock.lock();
            if (instance == null) {
                instance = new DeadlockFreeLockManager();
            }
            instanceLock.unlock();
        }

        return instance;
    }

    public static synchronized boolean isDeadlockDetected(List<ResourceConsumingThread> threadsVisitedSoFar,
                                                          ResourceConsumingThread startingThread,
                                                          ResourceConsumingThread curThread) {


        for (ResourceConsumingThread dependencyThread : curThread.getDependencyList()
                ) {
            if(threadsVisitedSoFar.contains(dependencyThread) && (startingThread != curThread)){
                //deadlock
                return true;
            }else{
                threadsVisitedSoFar.add(curThread);
                return isDeadlockDetected(threadsVisitedSoFar, startingThread, dependencyThread);
            }
        }

        return false;
    }

    public void displayThreadRegistry(){
        for(ResourceConsumingThread curThread : threadRegistry){
            System.out.println("\n ------------------------------------------------");
            System.out.println("Thread ID : " + curThread.getCustomId() + " has below dependencies");
            for (ResourceConsumingThread dependencyThread : curThread.getDependencyList()){
                System.out.println(dependencyThread.toString());
            }

            System.out.println("Thread ID : " + curThread.getCustomId() + " has acquired below resources");
            for (Resource resource : curThread.getAcquiredResources()){
                System.out.println(resource.getId());
            }

            System.out.println("Thread ID : " + curThread.getCustomId() + " has requested below resources");
            for (Resource resource : curThread.getRequestedResources()){
                System.out.println(resource.getId());
            }
        }
    }

    public void cleanThreadRegistry() {
        for (ResourceConsumingThread t: threadRegistry) {
            t.clearResourceLists();
            t.clearDependencyLists();
        }

        ResourceConsumingThread.setIsDeadlockDetected(false);
    }
}
