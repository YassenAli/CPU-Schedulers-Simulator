package com.os.cpu_schedulers;

import java.util.*;

class ProcessTest {
    private String name;
    private String color;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int waitingTime;  // for iteration and solve starvation

    public ProcessTest(String name, String color, int arrivalTime, int burstTime, int priority , int waitingTime) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.waitingTime = waitingTime;
    }

    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }
    public int getWaitingTime(){
        return waitingTime;
    }

    public void incrementWaitCount() {
        waitingTime++;
    }

    public void increasePriority() {
        priority--;
    }

    public void resetWaitCount() {
        waitingTime = 0;
    }
}

public class test {
    private ArrayList<ProcessTest> processList;
    private static int contextSwitch;
    private Set<ProcessTest> processedSet;  // To know which processes have been added

    Scanner scanner = new Scanner(System.in);

    public test() {
        processedSet = new HashSet<>();

        System.out.println("Enter the number of processes:");
        int numberOfProcesses = scanner.nextInt();
        System.out.println("Enter the context switch time:");
        contextSwitch = scanner.nextInt(); // Use the static variable directly
        processList = new ArrayList<>();

        for (int i = 0; i < numberOfProcesses; i++) {
            System.out.println("Enter the name of the process:");
            String name = scanner.next();
            System.out.println("Enter the color of the process:");
            String color = scanner.next();
            System.out.println("Enter the arrival time of the process:");
            int arrivalTime = scanner.nextInt();
            System.out.println("Enter the burst time of the process:");
            int burstTime = scanner.nextInt();
            System.out.println("Enter the priority of the process:");
            int priority = scanner.nextInt();
            processList.add(new ProcessTest(name, color, arrivalTime, burstTime, priority , 0));
        }

        Collections.sort(processList, Comparator.comparingInt(ProcessTest::getArrivalTime));
        PriorityQueue<ProcessTest> waitingProcesses = new PriorityQueue<>(Comparator.comparingInt(ProcessTest::getPriority));
        int currentTime = 0;
        int avgWaitingTime = 0;
        int avgTurnAroundTime = 0;

        for (int i = 0; i < processList.size() || !waitingProcesses.isEmpty(); ) {
            // Add processes to the waiting queue that have arrived and are not yet processed
            while (i < processList.size() && processList.get(i).getArrivalTime() <= currentTime) {
                ProcessTest process = processList.get(i);
                if (!processedSet.contains(process)) {
                    waitingProcesses.add(process);
                    processedSet.add(process);
                    System.out.println("!!!! Process: " + process.getName() + " added to the waiting list");
                }
                i++;
            }

            // each waiting process has its own waiting time when this time == 5
            // increase the last process waiting priority by one and then make its waiting time = 0
            // put all waiting processes in temporary array
            // and check the waiting time for each and apply the aging then add all the processes in the actual waiting list

            ArrayList<ProcessTest> tempQueue = new ArrayList<>();

            while (!waitingProcesses.isEmpty()) {
                ProcessTest process = waitingProcesses.poll();
                process.incrementWaitCount();
                tempQueue.add(process);
            }


            if (!tempQueue.isEmpty()) {
                ProcessTest lastProcess = tempQueue.get(tempQueue.size() - 1);
                if (lastProcess.getWaitingTime() >= 5) {
                    lastProcess.increasePriority();
                    lastProcess.resetWaitCount();
                    System.out.println("Aging applied: Process " + lastProcess.getName() + " priority increased to " + lastProcess.getPriority());
                }
            }

            waitingProcesses.addAll(tempQueue);



            if (!waitingProcesses.isEmpty()) { // execute

                ProcessTest nextProcess = waitingProcesses.poll();
                currentTime += nextProcess.getBurstTime();
                System.out.println("Process: " + nextProcess.getName() + " executed from the waiting list");
                int waitingTime = currentTime - nextProcess.getArrivalTime() - nextProcess.getBurstTime();
                System.out.println("Waiting time is " + waitingTime);
                int turnAorundTime = currentTime-nextProcess.getArrivalTime();
                System.err.println("TurnAroundTime = " + turnAorundTime);

                avgWaitingTime += waitingTime;
                avgTurnAroundTime += turnAorundTime;

                // add the context switch if it is not the last process
                if (!waitingProcesses.isEmpty() || i < processList.size()) {
                    currentTime += contextSwitch;
                }
            } else if (i < processList.size()) {
                // in the idle case if the cpu has no process to do , current time = the next arrival time
                int idleTime = processList.get(i).getArrivalTime() - currentTime;
                System.out.println("CPU is idle for " + idleTime + " time units");
                currentTime =  processList.get(i).getArrivalTime();
            }
        }

        System.err.println("the current time is " + currentTime);
        System.err.println("Average Waiting Time = " + avgWaitingTime / processList.size());
        System.err.println("Average Turnaround Time = " + avgTurnAroundTime / processList.size());
    }

    public static void main(String args[]) throws Exception {
        new test();
    }
}

// calculate tha waiting time for each process  (the time at which executed - arrival time)
// Turnaround Time for each process
// Average Waiting Time
// Average Turnaround  Time