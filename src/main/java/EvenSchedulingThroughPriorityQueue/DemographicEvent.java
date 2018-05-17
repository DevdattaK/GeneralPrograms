package EvenSchedulingThroughPriorityQueue;

import java.time.Instant;

public class DemographicEvent extends Event{
    public DemographicEvent(Instant eventTimeStamp, EventOwner owner) {
        super(eventTimeStamp, owner);
    }

    @Override
    public String toString() {
        return super.toString() + this.getClass().getName() + " triggered by : " + this.getEvenOwnerAsString();
    }
}
