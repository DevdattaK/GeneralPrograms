package EvenSchedulingThroughPriorityQueue;

import java.util.Comparator;
import java.util.concurrent.Semaphore;


//all indexes are 0 based
public class MinHeapPriorityBuilder<T> {
    private T[] events;
    private Semaphore eventHeapMonitor;
    private int heapSize;
    private Comparator<T> comparator;


    public MinHeapPriorityBuilder(T[] events, int heapSize, Comparator eventTimestampComparator) {
        this.events = events;
        this.heapSize = heapSize;
        eventHeapMonitor = new Semaphore(1);
        this.comparator = eventTimestampComparator;
    }

    public int getHeapSize() {
        int result = -1;

        try {
            this.acquireLockOnEventHeap();

            result = heapSize;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.releaseLockOnEventHeap();
        }

        return result;
    }

    public void acquireLockOnEventHeap() throws InterruptedException {
        //System.out.println(" ++ Acquiring lock on eventHeapMonitor.");
        eventHeapMonitor.acquire();
    }

    public void releaseLockOnEventHeap() {
        eventHeapMonitor.release();
        //System.out.println(" -- Released lock on eventHeapMonitor.");
    }

    public void increaseHeapSize() {
        try {
            this.acquireLockOnEventHeap();

            heapSize++;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.releaseLockOnEventHeap();
        }

    }

    public  void decreaseHeapSize() {
        try {
            this.acquireLockOnEventHeap();

            heapSize--;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.releaseLockOnEventHeap();
        }
    }

    public void swap(int firstIndex, int secondIndex) {
        T temp = events[firstIndex];
        events[firstIndex] = events[secondIndex];
        events[secondIndex] = temp;
    }

    public void heapify(int index) {
        int left = index * 2 + 1;
        int right = index * 2 + 2;
        int targetIndexForSwap = -1;

        if (left <= heapSize && comparator.compare(events[left], events[index]) < 0) {
            targetIndexForSwap = left;
        } else {
            targetIndexForSwap = index;
        }

        if (right <= heapSize && comparator.compare(events[right], events[targetIndexForSwap]) < 0) {
            targetIndexForSwap = right;
        }

        if (targetIndexForSwap != index) {
            this.swap(index, targetIndexForSwap);
            this.heapify(targetIndexForSwap);
        }
    }

    public void buildHeap() {
        //System.out.println("Inside build heap." + Thread.currentThread()
         //       .getName());
        for (int i = heapSize / 2; i >= 0; i--) {
            this.heapify(i);
        }
    }

    public T extractTopAndRefreshHeap() throws Exception {
        T topOfPriorityHeap;

        this.acquireLockOnEventHeap();
        //System.out.println("Extracting next element from heap.");

        if (heapSize > -1) {
            this.buildHeap();

            //copy top into separate var, and then replace top with the lowest element.
            topOfPriorityHeap = events[0];
            events[0] = events[heapSize];
            events[heapSize] = null;
            --heapSize;

            //refresh the heap from top.
            this.heapify(0);

            this.releaseLockOnEventHeap();
        } else {
            //System.out.println("Heap is not populated with events.");
            throw new Exception("Heap not populated with events yet.");
        }

        //System.out.println("Next element to process : " + ((Event)topOfPriorityHeap).getEventId());

        return topOfPriorityHeap;
    }


    public void addNewEventToHeap(T event) {
        try {
            this.acquireLockOnEventHeap();
            //System.out.println("Heapsize before adding "+ ((Event)event).getEventId() + " is : " + heapSize);

            //acquire and release monitor permit.
            if (heapSize < events.length - 1) {
                //i.e. there is space to add this event.
                //System.out.println("Adding new event to heap..." + ((Event)event).getEventId());
                this.events[++heapSize] = event;

                this.buildHeap();
                System.out.println("New Heapsize after adding " + ((Event)event).getEventId() + " for rescheduling is : " + heapSize);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.releaseLockOnEventHeap();
        }
    }

    public T[] getEvents() {
        return events;
    }
}
