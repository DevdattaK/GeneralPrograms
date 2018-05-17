package ElevatorSimulation;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator implements Runnable {

    private int elevatorId;

    public enum tState {IN_USE, WAITING_FOR_NEXT_REQ}

    private tState state;

    public enum tDoorState {DOOR_OPEN, DOOR_CLOSED}

    private volatile tDoorState doorState;
    private Floor atFloor;
    private Floor nextFloor;
    private Set<ElevatorUser> carryingUsers;
    //private ReentrantLock carryingUsersLock;
    /*filled by individuals inside the elevator when the press drop-off button. treeSet is sorted,
        Comparator for sort should use the Direction to decide ascending/descending order*/
    private Map<Floor, Set<ElevatorUser>> floorStopsForUsers;
    private ReentrantLock floorStopRequestLock;
    private ElevatorControllerSystem controllerSystem;
    private boolean isElevatorRegisteredWithControllerSystem = false;
    private int currentTripCount = 0;
    private final int maxTripCount = 10;
    private Semaphore nextFloorNotificationSemaphore;


    public Elevator(int elevatorId) {
        this.elevatorId = elevatorId;
        this.carryingUsers = new HashSet<>();
        this.state = tState.WAITING_FOR_NEXT_REQ;
        this.floorStopRequestLock = new ReentrantLock();
        this.floorStopsForUsers = new TreeMap<>(Comparator.comparing(Floor::getNumber));
        // this.carryingUsersLock = new ReentrantLock();
        this.nextFloorNotificationSemaphore = new Semaphore(1);
    }

    public Elevator(int elevatorId, Floor atFloor) {
        this(elevatorId);
        this.atFloor = atFloor;
    }


    public int getId() {
        return elevatorId;
    }


    public ElevatorControllerSystem getControllerSystem() {
        return controllerSystem;
    }

    public void setAtFloor_Test(Floor dropOffFloor) {
        this.atFloor = dropOffFloor;
    }


    public void acceptRequestedDroppOff(Floor requestedDropAtFloor, ElevatorUser forUser) {
        if (this.floorStopsForUsers.get(requestedDropAtFloor) != null) {
            this.floorStopsForUsers.get(requestedDropAtFloor)
                    .add(forUser);
        } else {
            Set<ElevatorUser> users = new HashSet<>();
            users.add(forUser);
            this.floorStopsForUsers.put(requestedDropAtFloor, users);
        }
    }

    public void notifyExited(ElevatorUser user) {
        floorStopRequestLock.lock();

        carryingUsers.remove(user);

        if (floorStopsForUsers.get(this.atFloor)
                .size() <= 1) {
            floorStopsForUsers.remove(this.atFloor);
            this.doorState = tDoorState.DOOR_CLOSED;
            atFloor = null;
        } else {
            floorStopsForUsers.computeIfPresent(this.atFloor, (floor, users) -> {
                users.remove(user);
                return users;
            });
        }

        floorStopRequestLock.unlock();
    }


    public void registerWithControllerSystem(ElevatorControllerSystem controllerSystem) {
        if (!isElevatorRegisteredWithControllerSystem) {
            this.controllerSystem = controllerSystem;
            this.controllerSystem.getElevators()
                    .add(this);
        }
    }

    public Floor getAtFloor() {
        return atFloor;
    }

    public tDoorState getDoorState() {
        return doorState;
    }

    public void acceptUser(ElevatorUser user) {
        this.carryingUsers.add(user);
    }


    public void updateStateTo_ForTest(tDoorState doorState) {
        this.doorState = doorState;
    }

    public boolean isStopRequestedAt(int floorNum) {
        return floorStopsForUsers.keySet()
                .stream()
                .mapToInt(e -> e.getNumber())
                .anyMatch(i -> i == floorNum);
    }

    public void goToNextFloor() throws InterruptedException {
        System.out.println("Simulating elevator movement time...");
        this.state = tState.IN_USE;
        TimeUnit.SECONDS.sleep((new Random()).nextInt(5));

        this.atFloor = this.nextFloor;

        System.out.println("Is NextFloor Null : " + (this.atFloor == null));

        System.out.println("Elevator " + elevatorId + " reached to Floor " + nextFloor.getNumber() + ". All users, do your task now.");

        synchronized (this.atFloor) {
            this.doorState = tDoorState.DOOR_OPEN;

            this.atFloor.getWaitingUsers()
                    .stream()
                    .forEach(u -> u.notifyUserAboutArrivalOfElevator());

            controllerSystem.notifyTaskCompletedByElevatorOnFloor(this, this.atFloor);

            this.nextFloor = null;

            TimeUnit.SECONDS.sleep((new Random()).nextInt(5));
        }
    }

    public Floor getNextFloor() {
        return nextFloor;
    }

    //this method is called by elevator to get the next floor to stop. This comes from controllerSystem and not from user.
    public Floor getNextFloorFromControllerSystem() {
        System.out.println("Elevator was out of work hence requested next stop from the controllerSystem.");
        this.nextFloor = this.controllerSystem.getNextStopRequestInQueue(this);

        /*floorStopRequestLock.lock();

        if (floorStopsForUsers.get(this.nextFloor) == null) {
            floorStopsForUsers.put(this.nextFloor, new HashSet<>());
        } else {
            //do nothing, as the elevator is already making a stop at requested floor due to user's request.
        }

        floorStopRequestLock.unlock();*/

        return this.nextFloor;
    }

    public Semaphore getNextFloorNotificationSemaphore() {
        return nextFloorNotificationSemaphore;
    }

    @Override
    public String toString() {
        return "Elevator " + elevatorId + " carrying " + carryingUsers.size() + " number of peeps and in state : " + state;
    }

    //this method gets the floor, first from user requested stops. If that is empty, then it requests from the controller system and blocks.
    public Floor getNextLogicalFloorForStop() throws InterruptedException {
        Floor nextLogicalStop = null;

        this.floorStopRequestLock.lock();

        if (this.floorStopsForUsers.keySet()
                .size() == 0) {
            this.state = tState.WAITING_FOR_NEXT_REQ;
            //for elevator, trip count is number of times it had to request controllerSystem for next floor stop.
            this.currentTripCount++;

            System.out.println("Elevator " + elevatorId + " requesting next floor from controlSystem ");

            nextFloorNotificationSemaphore.acquire();
            this.nextFloor = this.getNextFloorFromControllerSystem();

            nextFloorNotificationSemaphore.acquire();
            nextFloorNotificationSemaphore.release();
        } else {
            //get the next stop requested from the user-requested stop list.

            this.nextFloor = this.getNextClosestFloorFromUserRequestedStopList();

            System.out.println("Going to user requested stop : ");
        }

        this.floorStopRequestLock.unlock();

        return nextLogicalStop;
    }

    private Floor getNextClosestFloorFromUserRequestedStopList() {
        floorStopRequestLock.lock();

        Floor nextClosestFloor;

        if(floorStopsForUsers.keySet().size() > 1) {
            Floor before = floorStopsForUsers.keySet()
                    .stream()
                    .sorted(Comparator.comparing(Floor::getNumber)
                            .reversed())
                    .filter(f -> f.getNumber() < this.atFloor.getNumber())
                    .findFirst()
                    .get();

            Floor after = floorStopsForUsers.keySet()
                    .stream()
                    .sorted(Comparator.comparing(Floor::getNumber)
                            .reversed())
                    .filter(f -> f.getNumber() > this.atFloor.getNumber())
                    .findFirst()
                    .get();

            nextClosestFloor = (this.atFloor.getNumber() - before.getNumber()) < (after.getNumber() - this.atFloor.getNumber()) ? before : after;
        }else{
            nextClosestFloor = floorStopsForUsers.keySet().stream().findFirst().get();
        }

        floorStopRequestLock.unlock();

        return nextClosestFloor;
    }

    @Override
    public void run() {
        while (currentTripCount < maxTripCount) {
            System.out.println(this);
            try {
                this.getNextLogicalFloorForStop();

                this.goToNextFloor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
