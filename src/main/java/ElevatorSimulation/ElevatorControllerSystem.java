package ElevatorSimulation;


import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class ElevatorControllerSystem {
    private int controllerSystemId;
    private static int controllerSystemIdGenerator = 1;
    private Set<Floor> floors;
    private Set<Elevator> elevators;
    private TreeSet<Floor> stopRequestSet;                  //need synchronized access.
    private ReentrantLock stopRequestSetLock;
    private static ElevatorControllerSystem instance;
    private Semaphore stopRequestMonitor;


    private ElevatorControllerSystem() throws InterruptedException {
        controllerSystemId = controllerSystemIdGenerator++;
        floors = new HashSet<>();
        elevators = new HashSet<>();
        stopRequestSet = new TreeSet<>(Comparator.comparing(Floor::getNumber)); //keep the floors sorted by Floor#.
        stopRequestSetLock = new ReentrantLock();
        this.stopRequestMonitor = new Semaphore(1);
        this.stopRequestMonitor.acquire();
    }

    public synchronized static ElevatorControllerSystem getInstance() {
        if (instance == null) {
            try {
                instance = new ElevatorControllerSystem();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Interrupted Exception Occurred while creating ControllerSystem singleton. Originating from constructor.");
            }
        }

        return instance;
    }

    public static Floor getRandomFloorExcept(Floor atFloor) {
        int randomFloor = atFloor.getNumber();

        while (randomFloor == atFloor.getNumber()) {
            randomFloor = ThreadLocalRandom.current()
                    .nextInt(1, instance.floors.size());
        }

        final int finalRandomFloor = randomFloor;

        Optional<Floor> floor = instance.getFloors()
                .stream()
                .filter(f -> f != atFloor && f.getNumber() == finalRandomFloor)
                .findFirst();


        return floor.orElseGet(() -> instance.getFloors()
                .stream()
                .findFirst()
                .get());
    }

    public Set<Floor> getFloors() {
        return floors;
    }

    public Set<Elevator> getElevators() {
        return elevators;
    }


    public void registerElevatorRequestForFloor(Floor atFloor) {
        stopRequestSetLock.lock();

        stopRequestSet.add(atFloor);
        stopRequestMonitor.release();

        stopRequestSetLock.unlock();
    }

    public TreeSet<Floor> getStopRequestSet() {
        return stopRequestSet;
    }

    //for test purpose.
    public void addFloorForTest(Floor floor) {
        stopRequestSetLock.lock();

        stopRequestSet.add(floor);

        stopRequestSetLock.unlock();
    }

    //to be replaced by Strategy Pattern.
    public Floor getNextStopRequestInQueue(Elevator elevator) {
        Floor result = null;

        try {
            stopRequestSetLock.lock();

            //permit is only available, when there is at least one stop request in the stopRequestSet. Otherwise, it waits. i.e. the calling elevator(s) wait/s.
            stopRequestMonitor.acquire();

            Floor before, after, curFloor = elevator.getAtFloor();
            if (stopRequestSet.size() > 1) {
                before = stopRequestSet.floor(curFloor);
                after = stopRequestSet.ceiling(curFloor);

                result = (curFloor.getNumber() - before.getNumber()) < (after.getNumber() - curFloor.getNumber()) ? before : after;
            }else{
                result = stopRequestSet.first();
            }

        }catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            stopRequestSetLock.unlock();
            elevator.getNextFloorNotificationSemaphore()
                    .release();
        }


        return result;
    }

    @Override
    public String toString() {
        return "ElevatorControllerSystem manages " + floors.size() + " floors and " + elevators.size() + " elevators. \n " +
                "\n Floor Status --------- \n " + floors.stream()
                .map(f -> f.toString())
                .collect(Collectors.joining("\n ")) +
                "\n Elevator Status --------- \n " + elevators.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining("\n ")) +
                "\n Stop Requests ----------- \n" + stopRequestSet.stream()
                .map(f -> "Stop requested at floor " + f.getNumber() + " by " + f.getWaitingUsers()
                        .size() + " users.")
                .collect(Collectors.joining("\n"));
    }

    //this method removes the floor which is already served from the stopRequestSet list.
    public void notifyTaskCompletedByElevatorOnFloor(Elevator elevator, Floor nextFloor) {
        stopRequestSetLock.lock();

        stopRequestSet.remove(nextFloor);

        stopRequestSetLock.unlock();
    }
}
