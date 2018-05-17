package EvenSchedulingThroughPriorityQueue;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
class that generates a stream of events
 */
public class EventGenerator {
    private final Map<String, Supplier<EventOwner>> ownerMap;
    //private final Map<String, Event> eventMap;


    public EventGenerator() {
        this.ownerMap = new HashMap<>();
        ownerMap.put("Billing", Billing::new);
        ownerMap.put("Payment", Payment::new);
        ownerMap.put("DemographicOperation", DemographicOperation::new);
    }


    public Stream<Event> getStreamOfEvents() {

        Function<String, Event> eventGeneratorFromString = (str) -> {
            int milliOffset = ThreadLocalRandom.current().nextInt(0, 10);

            try {

                String[] constructorParams = str.split(",");
                Supplier<EventOwner> supplier = ownerMap.get(constructorParams[1]);

                //reflective construction to avoid conditional operators
                Class cls = Class.forName(constructorParams[0]);
                Constructor ctor = cls.getConstructor(Instant.class, EventOwner.class);
                //System.out.println("MillisToAdd : " + milliOffset);

                //simulating delays in real life streams.
                Thread.sleep(123);

                return (Event) ctor.newInstance(Instant.now(), supplier.get());

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        };

        try  {
            Stream<String> eventStream = Files.lines(Paths.get("C:\\Users\\Kanawade_D\\IdeaProjects\\practicePrograms\\src\\main\\java\\EvenSchedulingThroughPriorityQueue\\DummyEventParameters.txt"), Charset.defaultCharset());
            return eventStream.filter(e -> e.length() > 0).map(eventGeneratorFromString);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //to avoid auto-closure of stream.
        }

        return null;
    }
}
