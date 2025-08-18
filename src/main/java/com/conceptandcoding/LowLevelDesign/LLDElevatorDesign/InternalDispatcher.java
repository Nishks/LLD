package com.conceptandcoding.LowLevelDesign.LLDElevatorDesign;

import java.util.List;

public class InternalDispatcher {

    List<ElevatorController>  elevatorControllerList = ElevatorCreator.elevatorControllerList;

    public void submitInternalRequest(int floor, ElevatorCar elevatorCar){
        for (ElevatorController controller : elevatorControllerList) {
            if (controller.elevatorCar.id == elevatorCar.id) {
                controller.submitInternalRequest(floor);
                break;
            }
        }
    }
}
