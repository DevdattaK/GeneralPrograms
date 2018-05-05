package DiningPhilosopherImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DiningPhilosopher {
    //chopsticks on table
    private final List<Chopstick> chopsticks;
    private final List<Philosopher> philosophers;
    private final int NUMBER_OF_PHILOSOPHERS = 5;

    public DiningPhilosopher() {
        chopsticks = new ArrayList<>(NUMBER_OF_PHILOSOPHERS);
        philosophers = new ArrayList<>(NUMBER_OF_PHILOSOPHERS);
    }

    public void setupDiningPhilosopherProblem(){
        IntStream.rangeClosed(0, NUMBER_OF_PHILOSOPHERS-1).forEach(i -> chopsticks.add(i, new Chopstick(i)));
        IntStream.rangeClosed(0, NUMBER_OF_PHILOSOPHERS-1).forEach(i -> philosophers.add(new Philosopher(i, chopsticks.get(i), chopsticks.get((i+1)%5))));
    }


    public void displayCurrentStateOfDiningTable(){
        philosophers.stream().forEach(p -> p.displayState());
    }

    public List<Philosopher> getPhilosophers() {
        return philosophers;
    }

    public int getNUMBER_OF_PHILOSOPHERS() {
        return NUMBER_OF_PHILOSOPHERS;
    }
}
