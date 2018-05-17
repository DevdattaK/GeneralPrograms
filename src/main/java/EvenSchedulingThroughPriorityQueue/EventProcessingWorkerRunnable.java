package EvenSchedulingThroughPriorityQueue;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class EventProcessingWorkerRunnable implements Callable {
    private Event event;

    public EventProcessingWorkerRunnable(Event event) {
        this.event = event;
    }

    private void processEvent() throws InterruptedException {
        if (event.getStatus() != Event.tStatus.COMPLETE | event.getStatus() != Event.tStatus.PROCESSING) {
            event.setStatus(Event.tStatus.PROCESSING);

            //System.out.println(Thread.currentThread().getName() + " is processing" + event);

            TimeUnit.MILLISECONDS.sleep(10);

            // aborting all events, whose timestamp is mod 5
            if (event.getEventTimeStamp().get(ChronoField.MILLI_OF_SECOND) % 5 == 0){
                event.setStatus(Event.tStatus.FAIL);
                event.incrementCurrentAttemptNum();
            }else {
                event.setStatus(Event.tStatus.COMPLETE);
                event.resetCurrentAttempNumber();
            }

            //System.out.println("Processing finished for : " + event.getEventId() + " with status : " + event.getStatus());

        }else{
            System.out.println("Event can not be processed." + event);
        }
    }


    @Override
    public Event call() throws Exception {
        if(!Thread.interrupted()) {
            processEvent();
        }else{
            //restore the state of event, as it was before.
            event.setStatus(Event.tStatus.PUSHED_LATER_BY_PREEMPTION);
        }

        return event;
    }
}
