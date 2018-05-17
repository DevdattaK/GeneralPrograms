import EvenSchedulingThroughPriorityQueue.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EventSchedulingTest {
    private EventScheduler<Event> scheduler;
    private final int maxEventsToStartWith = 5;

    @BeforeEach
    void setUp() {
        /*int currentSize = -1;
        Instant currentInstant = Instant.now();
        EventOwner[] owners = {new DemographicOperation(), new Billing(), new Payment()};
        Event[] events = {new DemographicEvent(currentInstant, owners[0]),
                new AccountingEvent(currentInstant.plus(5, ChronoUnit.MILLIS), owners[1]),
                new AccountingEvent(currentInstant.plus(10, ChronoUnit.MILLIS), owners[2])
        };

        Supplier<Event[]> eventSupplier = () -> events;


        Comparator<Event> timestampComparator = Comparator.comparing(Event::getEventTimeStamp);
        MinHeapPriorityBuilder<Event> priorityBuilder = new MinHeapPriorityBuilder<>(events, currentSize, timestampComparator);

        scheduler = EventScheduler.getInstance(priorityBuilder, eventSupplier);*/
    }

    @Test
    void simulationTest() {
        try {
            ProcessingSimulator.simulate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSemaphoreRelease() throws InterruptedException {

    }

    @Test
    void eventStreamGenerationTest() {
        EventGenerator generator = new EventGenerator();
        Stream<Event> eventStream = generator.getStreamOfEvents();
        eventStream.filter(e -> e != null).forEach(System.out::println);
    }


}
