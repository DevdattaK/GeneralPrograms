package EvenSchedulingThroughPriorityQueue;

import java.time.Instant;

public class Event {
    private final String eventId;
    private static int idGenerator = 1;
    private /*final*/ Instant eventTimeStamp;
    public enum tStatus {PROCESSING, SCHEDULED, PUSHED_LATER_BY_PREEMPTION, COMPLETE, FAIL}
    private tStatus status;
    private final EventOwner owner;
    private int currentAttemptNum;


    public Event(Instant eventTimeStamp, EventOwner owner) {
        this.eventId = "Event#" + idGenerator++;
        this.eventTimeStamp = eventTimeStamp;
        this.owner = owner;
        currentAttemptNum = 0;
    }

    public tStatus getStatus() {
        return status;
    }

    public void setStatus(tStatus status) {
        this.status = status;
    }

    public Instant getEventTimeStamp() {
        return eventTimeStamp;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEvenOwnerAsString(){
        return owner.getClass().getName();
    }


    public boolean isProcessedSuccessfully(){
        return status == tStatus.COMPLETE;
    }


    public int getCurrentAttemptNum() {
        return currentAttemptNum;
    }

    public void incrementCurrentAttemptNum(){
        currentAttemptNum++;
    }

    public void resetCurrentAttempNumber() {
        currentAttemptNum = 0;
    }

    public void ONLYFORSIMULATION_setTimestamp(Instant timestamp){
        this.eventTimeStamp = timestamp;
    }

    @Override
    public String toString() {
        return eventId + " with timestamp : " + eventTimeStamp +". Event Type : ";
    }

}
