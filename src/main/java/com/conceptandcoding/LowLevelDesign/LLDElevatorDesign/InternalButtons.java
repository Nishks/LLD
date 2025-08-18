package com.conceptandcoding.LowLevelDesign.LLDElevatorDesign;


import java.util.Arrays;

public class InternalButtons {

    InternalDispatcher dispatcher = new InternalDispatcher();

    int[] availableButtons = {1,2,3,4,5,6,7,8,9,10};
    int buttonSelected;

    void pressButton(int destination, ElevatorCar elevatorCar) {

        //1.check if destination is in the list of available floors
        boolean valid = Arrays.stream(availableButtons).anyMatch(f -> f == destination);

        if(!valid){
            System.out.println("Invalid floor: " + destination);
            return;
        }

        System.out.println("Go to floor : " + destination);


        //2.submit the request to the jobDispatcher
        dispatcher.submitInternalRequest(destination, elevatorCar);
    }

}
