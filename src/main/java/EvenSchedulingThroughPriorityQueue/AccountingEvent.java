package EvenSchedulingThroughPriorityQueue;

import java.time.Instant;

public class AccountingEvent extends Event{

    public AccountingEvent(Instant eventTimeStamp, EventOwner owner) {
        super(eventTimeStamp, owner);
    }

    @Override
    public String toString() {
        return super.toString() + this.getClass().getName() + ", Accounting Operation : " + this.getEvenOwnerAsString();
    }
}
