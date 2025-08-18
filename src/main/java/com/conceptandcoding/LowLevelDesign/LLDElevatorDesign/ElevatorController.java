package com.conceptandcoding.LowLevelDesign.LLDElevatorDesign;

import java.util.PriorityQueue;

public class ElevatorController {

    PriorityQueue<Integer> upMinPQ;      // ascending (closest higher floor first)
    PriorityQueue<Integer> downMaxPQ;    // descending (closest lower floor first)
    ElevatorCar elevatorCar;

    ElevatorController(ElevatorCar elevatorCar){
        this.elevatorCar = elevatorCar;
        upMinPQ = new PriorityQueue<>();
        downMaxPQ = new PriorityQueue<>((a,b) -> b - a);
    }

    // Called by ExternalDispatcher
    public synchronized void submitExternalRequest(int floor, Direction direction) {
        if (direction == Direction.UP) {
            if (!upMinPQ.contains(floor)) upMinPQ.offer(floor);
        } else {
            if (!downMaxPQ.contains(floor)) downMaxPQ.offer(floor);
        }
        notifyAll();
    }

    // Called by InternalDispatcher/InternalButtons (same handling)
    public synchronized void submitInternalRequest(int floor) {
        if (floor > elevatorCar.currentFloor) {
            if (!upMinPQ.contains(floor)) upMinPQ.offer(floor);
        } else if (floor < elevatorCar.currentFloor) {
            if (!downMaxPQ.contains(floor)) downMaxPQ.offer(floor);
        } else {
            // already on floor
            System.out.println("Elevator " + elevatorCar.id + " already on requested floor " + floor);
        }
        notifyAll();
    }

    // Core control loop to be run in a thread
    public void controlElevator() {
        while (true) {
            Integer next = null;
            boolean goingUp = false;

            synchronized (this) {
                // priority: serve up queue first if elevator direction is up or idle
                if (!upMinPQ.isEmpty()) {
                    next = upMinPQ.poll();
                    goingUp = true;
                } else if (!downMaxPQ.isEmpty()) {
                    next = downMaxPQ.poll();
                    goingUp = false;
                } else {
                    // nothing to do -> become idle and wait
                    elevatorCar.elevatorState = ElevatorState.IDLE;
                    try {
                        wait(); // wait for new requests
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
            }

            if (next != null) {
                elevatorCar.elevatorState = ElevatorState.MOVING;
                elevatorCar.elevatorDirection = goingUp ? Direction.UP : Direction.DOWN;
                boolean arrived = elevatorCar.moveToFloor(next);
                if (!arrived) {
                    // If failed for some reason, re-enqueue
                    synchronized (this) {
                        if (goingUp) upMinPQ.offer(next);
                        else downMaxPQ.offer(next);
                    }
                } else {
                    // arrived -> optionally we could trigger internal simulation
                    // For simplicity, nothing else
                }
            }
        }
    }
}
