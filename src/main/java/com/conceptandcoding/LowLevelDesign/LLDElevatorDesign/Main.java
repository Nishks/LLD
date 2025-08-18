package com.conceptandcoding.LowLevelDesign.LLDElevatorDesign;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String args[]) throws InterruptedException {

        List<Floor> floorList = new ArrayList<>();
        int TOTAL_FLOORS = 11;
        for (int i = 0; i < TOTAL_FLOORS; i++) {
            floorList.add(new Floor(i));
        }

        Building building = new Building(floorList);

        // Start each elevator controller in its own thread
        for (ElevatorController controller : ElevatorCreator.elevatorControllerList) {
            new Thread(controller::controlElevator).start();
        }

        floorList.get(5).pressButton(Direction.UP);
        Thread.sleep(100);
        ElevatorCar first = ElevatorCreator.elevatorControllerList.get(0).elevatorCar;
         System.out.println("Simulating internal press: inside elevator " + first.id + " pressing 10");
        first.internalButtons.pressButton(10,first);
        Thread.sleep(200);

        floorList.get(4).pressButton(Direction.DOWN); // floor 5 wants down
        Thread.sleep(300);
        ElevatorCar second = ElevatorCreator.elevatorControllerList.get(1).elevatorCar;
        System.out.println("Simulating internal press: inside elevator " + second.id + " pressing 1");
        first.internalButtons.pressButton(1,second);


        Thread.sleep(1000);

    }
}
