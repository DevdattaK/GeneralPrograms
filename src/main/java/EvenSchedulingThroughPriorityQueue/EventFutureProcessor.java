package EvenSchedulingThroughPriorityQueue;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/*
    Threads which process the futures created while processing events. Preferably 2 threads should be started.
 */
public class EventFutureProcessor implements Runnable {
    private final EventScheduler scheduler;

    public EventFutureProcessor(EventScheduler scheduler) {
        this.scheduler = scheduler;
    }


    public void rescheduleTheEvent(Event event) {
        scheduler.rescheduleEventForExecution(event);
    }

    @Override
    public void run() {
        while (scheduler.moreEventsWaitingForProcessingToComplete()) {
            try {
                //lock released, when at least one future is available.
                this.processEventFuturesAndAdjustPriorityHeap();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    private void processEventFuturesAndAdjustPriorityHeap() throws InterruptedException, ExecutionException {
        Future<Event> eventFuture = scheduler.getNextFuture();      //blocking call. Locks futures, to get the first future from the list.

        Event event = eventFuture.get();

        if (event.getStatus() == Event.tStatus.FAIL  ) {
            if(scheduler.hasNotExceededMaxAttempts(event)) {
                //simulate the timestamp change, so that same even wont fail again.
                event.ONLYFORSIMULATION_setTimestamp(Instant.now());
                event.incrementCurrentAttemptNum();
                event.setStatus(Event.tStatus.SCHEDULED);
                this.rescheduleTheEvent(event);
                System.out.println("Rescheduling : " + event);
            }else{
                System.out.println(event.getEventId() + " exceeded max retries.");
            }
        } else {
            System.out.println("Successful processing of : " + event);
        }
    }
}
