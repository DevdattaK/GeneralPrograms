package ElevatorSimulation;

import java.awt.datatransfer.FlavorListener;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ElevatorUser implements Runnable {
    private int id;
    private static int idGenerator = 1;
    private double weight;
    private Floor atFloor;
    private final int maxNumberOfRides = 5;
    private int currentRideCount = 0;
    public enum tState {ON_FLOOR, WAITING_FOR_ELEVATOR, IN_ELEVATOR, WAITING_FOR_DROPOFF}
    private tState currentState;
    private Semaphore userOpLock;
    private Floor requestedDropAtFloor;           //only to be set by the users who are inside elevator.
    private Elevator associatedWithElevator;

    public ElevatorUser(double weight, Floor atFloor) {
        this.id = idGenerator++;
        this.currentState = tState.ON_FLOOR;
        this.atFloor = atFloor;
        this.weight = weight;
        this.userOpLock = new Semaphore(1);
    }


    public int getId() {
        return id;
    }


    public Floor getAtFloor() {
        return atFloor;
    }


    public tState getCurrentState() {
        return currentState;
    }


    @Override
    public String toString() {
        return "User " + id + " is at Floor " + atFloor.getNumber() + " in state : " + currentState;
    }

    public void incrementCurrentRideCount() {
        currentRideCount++;
    }

    public void setAssociatedWithElevator_Test(Elevator elevator) {
        this.associatedWithElevator = elevator;
    }


    public void requestElevatorAtFloor() {
        this.atFloor.requestElevator(this);
        this.currentState = tState.WAITING_FOR_ELEVATOR;
    }

    public Elevator getAssociatedWithElevator() {
        return associatedWithElevator;
    }

    public Floor getRequestedDropAtFloor() {
        return requestedDropAtFloor;
    }

    public void notifyUserAboutArrivalOfElevator(){
        userOpLock.release();
        System.out.println("userOpLock released for user " + id);
    }

    @Override
    public void run() {
        while (currentRideCount < maxNumberOfRides) {
            //request elevator at current floor
            //put this request on current floor through controllerSystem
            try {
                userOpLock.acquire();
                System.out.println("User " + id + " requested the elevator at floor " + atFloor.getNumber() + " and is waiting for its arrival. userOpLock acquired.");
                this.requestElevatorAtFloor();
                //lock released when elevator reaches to requested floor.

                //when elevator arrives at requested floor, floor will release user's lock.
                userOpLock.acquire();
                this.associatedWithElevator = this.getAtFloor()
                        .getElevatorStoppedAtFloor();
                this.enterIntoElevator(this.associatedWithElevator);

                //when inside elevator, request destination floor
                this.specifyDestinationForDropOff();
                System.out.println("User " + id + " requested dropOff at Floor " + requestedDropAtFloor.getNumber());
                //userOpLock.release();

                //when destination floor comes, elevator signals it.
                //when exited the elevator, update current floor
                userOpLock.acquire();
                System.out.println("User " + id + " acquired userOpLock and is getting off the elevator.");
                this.getOffTheElevator();
                userOpLock.release();
                System.out.println("User " + id + " released userOpLock.");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            incrementCurrentRideCount();
            //wait for random time.
            try {
                TimeUnit.SECONDS.sleep((new Random()).nextInt(3));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Server User " + id + " for " + currentRideCount + " times");
        }

        System.out.println("User " + id + " has used max number of services for today.");
    }

    //synched on elevator, so that elevator doesn't take off while user is getting off of it.
    public void getOffTheElevator() {
        if(this.associatedWithElevator.getDoorState() == Elevator.tDoorState.DOOR_OPEN){
            synchronized (this.associatedWithElevator){
                if(this.associatedWithElevator.getDoorState() == Elevator.tDoorState.DOOR_OPEN){
                    this.currentState = tState.ON_FLOOR;
                    this.atFloor = this.requestedDropAtFloor;
                    this.requestedDropAtFloor = null;
                    this.associatedWithElevator.notifyExited(this);
                    this.associatedWithElevator = null;
                }
            }
        }
    }

    public void specifyDestinationForDropOff() {
        if(this.currentState == tState.IN_ELEVATOR){
            this.requestedDropAtFloor = ElevatorControllerSystem.getRandomFloorExcept(this.atFloor);
            this.associatedWithElevator.acceptRequestedDroppOff(this.requestedDropAtFloor, this);
            this.currentState = tState.WAITING_FOR_DROPOFF;
        }
    }


    //to be called only when elevator is stopped at requested floor. userOpLock will be released from floor/elevator.
    //synchronizing on elevator so that elevator doesn't take off while user is trying to enter into it.
    public void enterIntoElevator(Elevator elevator) {
        if (elevator.getDoorState() == Elevator.tDoorState.DOOR_OPEN) {
            synchronized (elevator) {
                if (elevator.getDoorState() == Elevator.tDoorState.DOOR_OPEN) {
                    System.out.println("User " + id + " entered into Elevator " + associatedWithElevator.getId());
                    elevator.acceptUser(this);
                    this.currentState = tState.IN_ELEVATOR;
                }
            }

        }
    }
}
