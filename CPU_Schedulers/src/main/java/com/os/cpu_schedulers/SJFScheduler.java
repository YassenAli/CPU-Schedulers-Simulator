package com.os.cpu_schedulers.Schedulers;

import com.os.cpu_schedulers.Process;
import java.util.ArrayList;
import java.util.List;

public class SJFScheduler implements Scheduler {
    private List<Process> processes;
    private List<Process> executionOrder = new ArrayList<>();
    private final int agingFactor = 1; // Increase priority by reducing burst time

    public SJFScheduler(List<Process> processes) {
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

        executionOrder.clear();

        System.out.println("Process\tBurst Time\tWaiting Time\tTurnaround Time");

        while (completed < processes.size()) {
            Process currentProcess = null;
            int shortestEffectiveBurstTime = Integer.MAX_VALUE;

            // Adjust burst times with aging
            for (Process process : processes) {
                if (!isCompleted[processes.indexOf(process)] && process.getArrivalTime() <= currentTime) {
                    int effectiveBurstTime = process.getBurstTime() - (currentTime - process.getArrivalTime()) / agingFactor;
                    if (effectiveBurstTime < shortestEffectiveBurstTime) {
                        currentProcess = process;
                        shortestEffectiveBurstTime = effectiveBurstTime;
                    }
                }
            }

            if (currentProcess == null) {
                currentTime++;
                continue;
            }

            int processIndex = processes.indexOf(currentProcess);
            currentProcess.setWaitingTime(currentTime - currentProcess.getArrivalTime());
            totalWaitTime += currentProcess.getWaitingTime();
            currentProcess.setStartTime(currentTime); // Set the start time of the process
            currentTime += currentProcess.getBurstTime();
            currentProcess.setTurnaroundTime(currentTime - currentProcess.getArrivalTime());
            totalTurnaroundTime += currentProcess.getTurnaroundTime();
            isCompleted[processIndex] = true;
            completed++;

            executionOrder.add(currentProcess);

            System.out.println(currentProcess.getName() + "\t\t" + currentProcess.getBurstTime() + "\t\t"
                    + currentProcess.getWaitingTime() + "\t\t" + currentProcess.getTurnaroundTime());
        }

        float averageWaitTime = (float) totalWaitTime / processes.size();
        float averageTurnaroundTime = (float) totalTurnaroundTime / processes.size();
        System.out.println("\nAverage Waiting Time: " + averageWaitTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
    }

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }
}
