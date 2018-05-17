package EvenSchedulingThroughPriorityQueue;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ProcessingSimulator {
    private static EventScheduler scheduler;

    private static void setup() {
        int currentSize = 19;
        EventGenerator eventGenerator = new EventGenerator();

        Supplier<Event[]> eventSupplier = () ->  eventGenerator.getStreamOfEvents().toArray(Event[]::new);

        Comparator<Event> timestampComparator = Comparator.comparing(Event::getEventTimeStamp);
        MinHeapPriorityBuilder<Event> priorityBuilder = new MinHeapPriorityBuilder<>(eventSupplier.get(), currentSize, timestampComparator);

        try {
            scheduler = EventScheduler.getInstance(priorityBuilder, eventSupplier);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void startEventFutureProcessor(Thread t1) {
        t1.start();
    }

    public static void simulate() throws InterruptedException {
        setup();

        //scheduler thread is managerial thread... it spawns additional thread for getting work done.
        Thread schedulerThread = new Thread(() -> {
            while (scheduler.hasMoreEventsToSchedule()) {
                //System.out.println("---from simulator : more events to schedule with heapsize : " + scheduler.getHeapSizeFromPriorityHeapBuilder() + "---");
                try {
                    scheduler.processNextPriorityEvent();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            try {
                scheduler.cleanUpExecutorService();
            } catch (InterruptedException e) {
                System.out.println("interrupt during executorService cleanup");
                e.printStackTrace();
            }
            System.out.println("Ending scheduler. Current HeapSize : " + scheduler.getHeapSizeFromPriorityHeapBuilder());
        }, "schedulerThread");
        schedulerThread.start();

        //in its own thread, terminates when no more futures are available. single threaded for now.
        Thread eventResultProcessingThread = new Thread(new EventFutureProcessor(scheduler), "futureProcessingThread");
        eventResultProcessingThread.start();

        schedulerThread.join();
        eventResultProcessingThread.join();
    }


}
