package EvenSchedulingThroughPriorityQueue;

import java.time.Instant;

public class DemographicOperation implements EventOwner {
    private final String operationName;
    private final Person performedBy;

    public DemographicOperation(String opName, Person performedBy) {
        operationName = opName;
        this.performedBy = performedBy;
    }

    public DemographicOperation(){
        this("Address Change Op", new Person());
    }
}
