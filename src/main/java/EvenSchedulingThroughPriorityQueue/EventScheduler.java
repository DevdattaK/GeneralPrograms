package EvenSchedulingThroughPriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Supplier;

//class that orders the events and schedules them across threads.
public class EventScheduler<T> {
    private T[] events;
    private MinHeapPriorityBuilder<T> priorityBuilder;
    private static EventScheduler instance;
    private ExecutorService executorService;
    private ArrayList<Future<Event>> futures;
    private Semaphore futuresLock;
    private final int maxNumOfProcessingTries = 3;
    private ArrayList<Event> eventsBeingProcessed;
    private Semaphore eventsBeingProcessedLock;

    private EventScheduler(MinHeapPriorityBuilder<T> priorityBuilder, Supplier<T[]> supplier) throws InterruptedException {
        this.priorityBuilder = priorityBuilder;
        events = priorityBuilder.getEvents(); //supplier.get();
        executorService = Executors.newFixedThreadPool(2);      //pass explicit threadFactory with distinct thread names.
        futures = new ArrayList<>();
        futuresLock = new Semaphore(1);
        eventsBeingProcessed = new ArrayList<>();
        eventsBeingProcessedLock = new Semaphore(1);
        futuresLock.acquire();                              //lock acquired, so that upon its release, the consumers could be notified.
    }

    public synchronized static <T> EventScheduler getInstance(MinHeapPriorityBuilder<T> priorityBuilder, Supplier eventSupplier) throws InterruptedException {
        if (instance == null)
            instance = new EventScheduler(priorityBuilder, eventSupplier);

        return instance;
    }

    public ArrayList<Future<Event>> getFutures() {
        return futures;
    }

    public void processNextPriorityEvent() throws Exception {

        try {
            if (priorityBuilder.getHeapSize() >= 0) {
                Event event = (Event) priorityBuilder.extractTopAndRefreshHeap();

                eventsBeingProcessedLock.acquire();
                eventsBeingProcessed.add(event);                //adding this event to transit
                eventsBeingProcessedLock.release();

                Future<Event> future = executorService.submit(new EventProcessingWorkerRunnable(event));

                futures.add(future);

                eventsBeingProcessedLock.acquire();
                eventsBeingProcessed.remove(event);             //removing this event from transit, as it is a future
                eventsBeingProcessedLock.release();

                this.releaseFuturesLock();
            } else {
                //System.out.println("Heap size is less than zero...wait for some time till there are more events on heap or close the business day.");
            }
        } finally {

        }


    }

    public int getFuturesSize() throws InterruptedException {
        int size = -1;

        this.acquireFuturesLock();

        size = this.futures.size();

        this.releaseFuturesLock();

        return size;
    }

    public int getHeapSizeFromPriorityHeapBuilder() {
        return priorityBuilder.getHeapSize();
    }

    public void acquireFuturesLock() throws InterruptedException {
        //System.out.println(" -- acquiring futuresLock");
        futuresLock.acquire();
    }

    public void releaseFuturesLock() {
        futuresLock.release();
        //System.out.println(" ++ released futuresLock");
    }

    public boolean hasNotExceededMaxAttempts(Event event) {
        return event.getCurrentAttemptNum() < maxNumOfProcessingTries;
    }

    public void rescheduleEventForExecution(T eventAfterProcessing) {
        this.priorityBuilder.addNewEventToHeap(eventAfterProcessing);
        //System.out.println("\n Number of events available on heap now : " + Arrays.stream(events).filter(e -> e != null).count());
    }

    public boolean hasMoreEventsToSchedule() {
        boolean hasMoreEventsToTakeCareOf = false;

        try {
            hasMoreEventsToTakeCareOf = priorityBuilder.getHeapSize() >= 0 || this.areFuturesPresentWithUnprocessedState();

            if(!(priorityBuilder.getHeapSize() >= 0)){
                //System.out.println("No more events to schedule, however # of FuturesAwaiting : " + this.getFuturesSize());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return hasMoreEventsToTakeCareOf;
    }

    public int getEventsInTransit() {
        int result = 0;
        try {
            eventsBeingProcessedLock.acquire();
            result = eventsBeingProcessed.size();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventsBeingProcessedLock.release();
        }
        return result;
    }

    public boolean moreEventsWaitingForProcessingToComplete() {
        boolean result = false;

        try {
            if (this.getEventsInTransit() > 0 || this.hasMoreEventsToSchedule()) {
                result = true;
            }else{
                System.out.println("No more events available for scheduling(?). Current heap size : " + this.getHeapSizeFromPriorityHeapBuilder());
                result = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean areFuturesPresentWithUnprocessedState() throws InterruptedException {
        return this.getFuturesSize() > 0;
    }

    //exclusive access to futures.
    public Future<Event> getNextFuture() throws InterruptedException {
        Future<Event> future;

        this.acquireFuturesLock();

        future = futures.remove(0);

        //this.releaseFuturesLock();

        return future;
    }

    public void cleanUpExecutorService() throws InterruptedException {
        System.out.println("cleanupExecutorService...");
        executorService.shutdown();

        executorService.awaitTermination(15, TimeUnit.MINUTES);
    }
}
