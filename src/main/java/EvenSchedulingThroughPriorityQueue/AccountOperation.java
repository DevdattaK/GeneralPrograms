package EvenSchedulingThroughPriorityQueue;

public interface AccountOperation extends EventOwner {
    default String getOperationName(){
        return this.getClass().getName();
    }

}
