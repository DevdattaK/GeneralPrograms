package ElevatorSimulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ElevatorTest {

    private ElevatorControllerSystem elevatorSystem;
    private final int maxFloors = 10;
    private final int maxElevators = 3;
    private Set<Floor> floors;
    private Set<Elevator> elevators;
    private Set<ElevatorUser> users;
    private final int numberOfUsers = 20;

    @BeforeEach
    void setUp() {
        floors = IntStream.rangeClosed(1, maxFloors)
                .mapToObj(i -> new Floor())
                .collect(Collectors.toSet());


        elevators = IntStream.rangeClosed(1, maxElevators)
                .mapToObj(i -> new Elevator(i))
                .collect(Collectors.toSet());

        users = IntStream.rangeClosed(1, numberOfUsers)
                .mapToObj(i -> new ElevatorUser(145, floors.stream()
                        .filter(f -> (f.getNumber() == ThreadLocalRandom.current()
                                .nextInt(1, maxFloors)))
                        .findFirst()
                        .orElseGet(() -> (Floor) floors.toArray()[0])))
                .collect(Collectors.toSet());

        floors.stream()
                .forEach(f -> f.registerWithControllerSystem(ElevatorControllerSystem.getInstance()));
        elevators.stream()
                .forEach(e -> e.registerWithControllerSystem(ElevatorControllerSystem.getInstance()));

        elevatorSystem = ElevatorControllerSystem.getInstance();
    }

    @Test
    void elevatorUserConstructorTest() {
        ElevatorUser user = new ElevatorUser(ThreadLocalRandom.current()
                .nextDouble(50, 200), new Floor());
        System.out.println(user);
        assertEquals(user.getId(), 1);
        assertEquals(user.getAtFloor()
                .getNumber(), 1);
    }

    @Test
    void elevatorControllerSystemSingletonTest() throws InterruptedException {
        List<ElevatorControllerSystem> objs = new ArrayList<>(2);

        Thread t1 = new Thread(() -> {
            objs.add(ElevatorControllerSystem.getInstance());
        });

        Thread t2 = new Thread(() -> {
            objs.add(ElevatorControllerSystem.getInstance());
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertEquals(objs.get(0), objs.get(1), "Singleton Failed.");
    }

    @Test
    void floorSetupTest() {
        //Set<Floor> floors = IntStream.rangeClosed(1, maxFloors).mapToObj(i -> new Floor()).collect(Collectors.toSet());
        floors.stream()
                .forEach(f -> assertTrue(f.getNumber() < 11 && f.getNumber() > 0, "Unexpected Floor Created"));
    }

    @Test
    void registerWithControllerSystemTest() {
        floors.stream()
                .forEach(f -> f.registerWithControllerSystem(ElevatorControllerSystem.getInstance()));
        ElevatorControllerSystem.getInstance()
                .getFloors()
                .stream()
                .forEach(f -> assertEquals(f.getControllerSystem(), ElevatorControllerSystem.getInstance(), "controller system not registered with floor#" + f.getNumber()));

        elevators.stream()
                .forEach(e -> e.registerWithControllerSystem(ElevatorControllerSystem.getInstance()));
        ElevatorControllerSystem.getInstance()
                .getElevators()
                .stream()
                .forEach(e -> assertEquals(e.getControllerSystem(), ElevatorControllerSystem.getInstance(), "controller system not registered with elevator#" + e.getId()));

    }

    @Test
    void elevatorSystemSetupTest() {
        System.out.println(ElevatorControllerSystem.getInstance());
    }

    @Test
    void userSetupTest() {
        users.stream()
                .forEach(System.out::println);
    }

    @Test
    void IfUserRequestedElevator_ThenLogRequestForStopWithControllerSystem() {
        users.stream()
                .findFirst()
                .get()
                .requestElevatorAtFloor();

        assertEquals(1, ElevatorControllerSystem.getInstance()
                .getStopRequestSet()
                .size(), "uncontrolled stop requests registered with controllerSystem");

        System.out.println(ElevatorControllerSystem.getInstance());
    }

    @Test
    void IfElevatorRequestedNextStop_ThenGiveTheClosestStopToElevatorCurrentLocation() {
        ElevatorUser user = users.stream()
                .findFirst()
                .get();

        Floor before = elevatorSystem.getFloors()
                .stream()
                .filter(f -> f.getNumber() < user.getAtFloor()
                        .getNumber())
                .sorted(Comparator.comparing(Floor::getNumber)
                        .reversed())
                .findFirst()
                .get();

        Floor after = elevatorSystem.getFloors()
                .stream()
                .filter(f -> f.getNumber() > user.getAtFloor()
                        .getNumber())
                .sorted(Comparator.comparing(Floor::getNumber))
                .findFirst()
                .get();

        elevatorSystem.addFloorForTest(before);
        elevatorSystem.addFloorForTest(after);

        Floor nextStop = elevatorSystem.getNextStopRequestInQueue(new Elevator(1, user.getAtFloor()));
        System.out.println("NextStop at Floor# : " + nextStop.getNumber());
        assertEquals(user.getAtFloor().getNumber() + 1, nextStop.getNumber());
        System.out.println("ElevatorSystem state : " + elevatorSystem);;
    }

    @Test
    void IfInElevator_ThenSpecifyDestinationForDropOff() {
        ElevatorUser user = users.stream()
                .findFirst()
                .get();

        user.requestElevatorAtFloor();
        assertEquals(user.getCurrentState(), ElevatorUser.tState.WAITING_FOR_ELEVATOR);

        Elevator elevator = elevators.stream().filter(e -> e.getId() == 2).findFirst().get();

        elevator.updateStateTo_ForTest(Elevator.tDoorState.DOOR_OPEN);

        user.enterIntoElevator(elevator);
        assertEquals(user.getCurrentState(), ElevatorUser.tState.IN_ELEVATOR);

        user.specifyDestinationForDropOff();
        assertEquals(user.getCurrentState(), ElevatorUser.tState.WAITING_FOR_DROPOFF);



    }

    @Test
    void IfUserRequestedDropOff_ThenDropOffFloorRegisteredWithElevator() {
        ElevatorUser user = users.stream()
                .findFirst()
                .get();

        user.requestElevatorAtFloor();
        assertEquals(user.getCurrentState(), ElevatorUser.tState.WAITING_FOR_ELEVATOR);

        Elevator elevator = elevators.stream().filter(e -> e.getId() == 2).findFirst().get();

        elevator.updateStateTo_ForTest(Elevator.tDoorState.DOOR_OPEN);
        user.setAssociatedWithElevator_Test(elevator);

        user.enterIntoElevator(elevator);
        assertEquals(user.getCurrentState(), ElevatorUser.tState.IN_ELEVATOR);

        user.specifyDestinationForDropOff();
        assertEquals(user.getCurrentState(), ElevatorUser.tState.WAITING_FOR_DROPOFF);

        assertNotNull(user.getAssociatedWithElevator());
        assertTrue(user.getAssociatedWithElevator().isStopRequestedAt(user.getRequestedDropAtFloor().getNumber()));

    }

    @Test
    void IfUserGotOffAtFloor_ThenElevatorAndUserStateUpdatedCorrectly() {
        ElevatorUser user = users.stream()
                .findFirst()
                .get();

        user.requestElevatorAtFloor();
        assertEquals(user.getCurrentState(), ElevatorUser.tState.WAITING_FOR_ELEVATOR);

        Elevator elevator = elevators.stream().filter(e -> e.getId() == 2).findFirst().get();

        elevator.updateStateTo_ForTest(Elevator.tDoorState.DOOR_OPEN);
        user.setAssociatedWithElevator_Test(elevator);

        user.enterIntoElevator(elevator);
        assertEquals(user.getCurrentState(), ElevatorUser.tState.IN_ELEVATOR);

        user.specifyDestinationForDropOff();
        Floor dropOffFloor = user.getRequestedDropAtFloor();
        elevator.setAtFloor_Test(dropOffFloor);
        assertEquals(user.getCurrentState(), ElevatorUser.tState.WAITING_FOR_DROPOFF);

        assertNotNull(user.getAssociatedWithElevator());
        assertTrue(user.getAssociatedWithElevator().isStopRequestedAt(user.getRequestedDropAtFloor().getNumber()));

        elevator.updateStateTo_ForTest(Elevator.tDoorState.DOOR_OPEN);
        user.getOffTheElevator();

        assertNull(user.getAssociatedWithElevator());
        assertNull(elevator.getAtFloor());
        assertEquals(user.getAtFloor(), dropOffFloor, "After DropOff, floor is not set currently for the user.");
    }

    @Test
    void IfUserRequestedElevator_ElevatorGetsThatAsNextStopIfIdle() throws InterruptedException {
        ElevatorUser user = users.stream()
                .findFirst()
                .get();

        Elevator elevator = elevators.stream().filter(e -> e.getId() == 2).findFirst().get();

        Thread userThread = new Thread(()->{
            user.requestElevatorAtFloor();
        });

        Thread elevatorThread = new Thread(()->{
            elevator.getNextFloorFromControllerSystem();
        });

        userThread.start();

        TimeUnit.SECONDS.sleep(5);

        elevatorThread.start();

        userThread.join();
        elevatorThread.join();

        assertEquals(user.getAtFloor(), elevator.getNextFloor(), "user requested elevator however levator went somewhere else.");
    }

    @Test
    void IfUserRequestedElevator_ThenElevatorGoesToThatFloorAndUsersEnterElevator_ThenElevatorGoesToDestination_ThenUserExits() throws InterruptedException {
        ElevatorUser user = users.stream()
                .findFirst()
                .get();
        Elevator elevator = elevators.stream().filter(e -> e.getId() == 2).findFirst().get();

        Thread userThread = new Thread(user);
        Thread elevatorThread = new Thread(elevator);

        userThread.start();

        TimeUnit.SECONDS.sleep(10);

        elevatorThread.start();

        userThread.join();
        elevatorThread.join();

        assertEquals(user.getAtFloor(), elevator.getNextFloor(), "user requested elevator however levator went somewhere else.");
    }
}
