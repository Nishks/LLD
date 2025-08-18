package com.conceptandcoding.LowLevelDesign.LLDElevatorDesign;

public class ElevatorCar {

    int id;
    ElevatorDisplay display;
    InternalButtons internalButtons;
    ElevatorState elevatorState;
    int currentFloor;
    Direction elevatorDirection;
    ElevatorDoor elevatorDoor;

    public ElevatorCar(){
        display = new ElevatorDisplay();
        internalButtons = new InternalButtons();
        elevatorState = ElevatorState.IDLE;
        currentFloor = 1;
        elevatorDirection = Direction.UP;
        elevatorDoor = new ElevatorDoor();
    }

    // Simulate movement. Returns true if arrived (simple simulation).
    public boolean moveToFloor(int destination) {
        System.out.println("Elevator " + id + " moving from " + currentFloor + " to " + destination);
        if (destination == currentFloor) {
            System.out.println("Elevator " + id + " already at floor " + currentFloor);
            elevatorDoor.openDoor();
            elevatorDoor.closeDoor();
            return true;
        }

        // determine direction
        Direction dir = destination > currentFloor ? Direction.UP : Direction.DOWN;
        elevatorDirection = dir;

        while (currentFloor != destination) {
            if (dir == Direction.UP) currentFloor++;
            else currentFloor--;

            // update display
            display.setDisplay(currentFloor, elevatorDirection);
            System.out.println("Elevator " + id + " now at floor " + currentFloor);
            display.showDisplay();

            // simulate travel time
            try {
                Thread.sleep(400); // travel time between floors
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Elevator " + id + " has arrived at floor " + currentFloor);
        elevatorDoor.openDoor();
        try { Thread.sleep(300); } catch (InterruptedException e) { /*ignored*/ }
        elevatorDoor.closeDoor();

        return true;
    }
}
