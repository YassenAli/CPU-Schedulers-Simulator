package com.os.cpu_schedulers.Schedulers;

import com.os.cpu_schedulers.Process;

import java.util.ArrayList;
import java.util.List;

public class SRTFScheduler implements Scheduler {
    private List<Process> processes;
    private List<Process> executionOrder = new ArrayList<>();
    private final int contextSwitchingTime = 1;

    public SRTFScheduler(List<Process> processes) {
        this.processes = processes;
    }

    @Override
    public void schedule(List<Process> processes) {
        this.processes = processes;
        calculateAndDisplay();
    }

    @Override
    public void printResults() {
        // Optional: Print summary results if required
    }

    public void calculateAndDisplay() {
        boolean[] isCompleted = new boolean[processes.size()];
        int completed = 0;
        int currentTime = 0;
        int totalWaitTime = 0;
        int totalTurnaroundTime = 0;
        int[] remainingBurstTime = new int[processes.size()];

        executionOrder.clear();

        for (int i = 0; i < processes.size(); i++) {
            remainingBurstTime[i] = processes.get(i).getBurstTime();
        }

        System.out.println("Process\tBurst Time\tWaiting Time\tTurnaround Time");

        while (completed < processes.size()) {
            Process currentProcess = null;
            int shortestRemainingTime = Integer.MAX_VALUE;
            int processIndex = -1;

            // Find the process with the shortest remaining time that is ready to execute
            for (int i = 0; i < processes.size(); i++) {
                Process process = processes.get(i);
                if (!isCompleted[i] && process.getArrivalTime() <= currentTime && remainingBurstTime[i] < shortestRemainingTime) {
                    currentProcess = process;
                    shortestRemainingTime = remainingBurstTime[i];
                    processIndex = i;
                }
            }

            if (currentProcess == null) {
                currentTime++; // If no process is ready, increment time
                continue;
            }

            // Simulate execution for 1 unit of time
            remainingBurstTime[processIndex]--;
            currentTime++;

            // If the process finishes execution, calculate its waiting and turnaround times
            if (remainingBurstTime[processIndex] == 0) {
                isCompleted[processIndex] = true;
                completed++;

                int turnaroundTime = currentTime - currentProcess.getArrivalTime();
                int waitingTime = turnaroundTime - currentProcess.getBurstTime();
                currentProcess.setWaitingTime(waitingTime);
                currentProcess.setTurnaroundTime(turnaroundTime);

                totalWaitTime += waitingTime;
                totalTurnaroundTime += turnaroundTime;

                executionOrder.add(currentProcess); // Add process to execution order once completed

                System.out.println(currentProcess.getName() + "\t\t" + currentProcess.getBurstTime() + "\t\t"
                        + waitingTime + "\t\t" + turnaroundTime);

                // Simulate context switching
                currentTime += contextSwitchingTime;
            }
        }

        // Calculate average waiting time and turnaround time
        float averageWaitTime = (float) totalWaitTime / processes.size();
        float averageTurnaroundTime = (float) totalTurnaroundTime / processes.size();
        System.out.println("\nAverage Waiting Time: " + averageWaitTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
    }

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }
}
