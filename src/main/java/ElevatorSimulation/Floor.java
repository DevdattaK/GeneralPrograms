package ElevatorSimulation;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Floor {
    private int number;
    private static int numberGenerator = 1;
    private ReentrantLock floorLock;
    private Set<ElevatorUser> waitingUsers;
    public enum tButtonType {UP, DOWN, NONE};
    private tButtonType elevatorRequestButton;
    private ElevatorControllerSystem controllerSystem;
    private boolean isElevatorRegisteredWithControllerSystem;

    public Floor(){
        this.number = numberGenerator++;
        floorLock = new ReentrantLock();
        this.elevatorRequestButton = tButtonType.NONE;
        waitingUsers = new HashSet<>();
    }


    public void requestElevator(ElevatorUser user) {
        controllerSystem.registerElevatorRequestForFloor(this);
        this.waitingUsers.add(user);
    }

    public Set<ElevatorUser> getWaitingUsers() {
        return waitingUsers;
    }

    public int getNumber(){
        return number;
    }


    public ElevatorControllerSystem getControllerSystem() {
        return controllerSystem;
    }


    @Override
    public String toString() {
        return "Floor# " + number + " has #of waiting users = " + waitingUsers.size() + " button state : " + elevatorRequestButton;
    }


    public void registerWithControllerSystem(ElevatorControllerSystem controllerSystem){
        if(!isElevatorRegisteredWithControllerSystem){
            this.controllerSystem = controllerSystem;
            this.controllerSystem.getFloors().add(this);
        }
    }


    public Elevator getElevatorStoppedAtFloor(){
        return controllerSystem.getElevators().stream().filter(e -> e.getAtFloor() == this).findFirst().get();
    }
}
