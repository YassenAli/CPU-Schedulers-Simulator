package com.os.cpu_schedulers.Schedulers;

import java.util.*;
import com.os.cpu_schedulers.Process;

public class FCAIScheduler implements Scheduler {
    private List<Process> processes;
    private double v1;
    private double v2;
    private Map<String, List<Integer>> quantumHistory; // Stores quantum updates for each process
    private double averageWaitingTime;
    private double averageTurnaroundTime;

    @Override
    public void schedule(List<Process> processes) {
        this.processes = new ArrayList<>(processes);
        this.quantumHistory = new HashMap<>();
        calculateV1AndV2();

        Queue<Process> readyQueue = new LinkedList<>();
        List<Process> completedProcesses = new ArrayList<>();
        int currentTime = 0;

        // Initialize processes with default quantum values and FCAI Factor
        for (Process process : processes) {
            process.setWaitingTime(0);
            process.setTurnaroundTime(0);
            quantumHistory.put(process.getName(), new ArrayList<>(List.of(2))); // Initial quantum value
        }

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            // Add arriving processes to the ready queue
            for (Iterator<Process> it = processes.iterator(); it.hasNext(); ) {
                Process process = it.next();
                if (process.getArrivalTime() <= currentTime) {
                    readyQueue.add(process);
                    it.remove();
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            // Calculate FCAI Factor for all processes in the ready queue
            List<Process> sortedQueue = new ArrayList<>(readyQueue);
            sortedQueue.sort(Comparator.comparingDouble(this::calculateFCAIFactor));

            // Execute the process with the highest priority (lowest FCAI Factor)
            Process currentProcess = sortedQueue.get(0);
            readyQueue.remove(currentProcess);

            int quantum = getCurrentQuantum(currentProcess);
            int nonPreemptiveTime = (int) (quantum * 0.4);
            int executedTime = 0;

            // Execute non-preemptively for 40% of quantum
            if (currentProcess.getRemainingTime() <= nonPreemptiveTime) {
                executedTime = currentProcess.getRemainingTime();
                currentProcess.setRemainingTime(0);
            } else {
                executedTime = nonPreemptiveTime;
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - executedTime);
            }

            currentTime += executedTime;

            // If process completes during non-preemptive execution
            if (currentProcess.getRemainingTime() == 0) {
                finalizeProcess(currentProcess, completedProcesses, currentTime);
                continue;
            }

            // Execute preemptively for the remaining quantum time
            int remainingQuantum = quantum - executedTime;
            if (currentProcess.getRemainingTime() <= remainingQuantum) {
                currentTime += currentProcess.getRemainingTime();
                currentProcess.setRemainingTime(0);
                finalizeProcess(currentProcess, completedProcesses, currentTime);
            } else {
                currentTime += remainingQuantum;
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - remainingQuantum);
                updateQuantum(currentProcess, false, remainingQuantum);
                readyQueue.add(currentProcess); // Re-add process to the queue
            }
        }

        calculateAndPrintResults(completedProcesses);
    }

    @Override
    public void printResults() {
        System.out.println("\nProcesses Execution Order and Quantum Updates:");
        for (Map.Entry<String, List<Integer>> entry : quantumHistory.entrySet()) {
            System.out.println("Process " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.printf("\nAverage Waiting Time: %.2f\n", averageWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f\n", averageTurnaroundTime);
    }

    private void calculateV1AndV2() {
        int lastArrivalTime = processes.stream().mapToInt(Process::getArrivalTime).max().orElse(1);
        int maxBurstTime = processes.stream().mapToInt(Process::getBurstTime).max().orElse(1);
        this.v1 = lastArrivalTime / 10.0;
        this.v2 = maxBurstTime / 10.0;
    }

    private double calculateFCAIFactor(Process process) {
        return (10 - process.getPriority()) +
                (process.getArrivalTime() / v1) +
                (process.getRemainingTime() / v2);
    }

    private int getCurrentQuantum(Process process) {
        List<Integer> history = quantumHistory.get(process.getName());
        return history.get(history.size() - 1);
    }

    private void updateQuantum(Process process, boolean completed, int unusedQuantum) {
        int newQuantum = completed ? getCurrentQuantum(process) + 2
                : getCurrentQuantum(process) + unusedQuantum;
        quantumHistory.get(process.getName()).add(newQuantum);
    }

    private void finalizeProcess(Process process, List<Process> completedProcesses, int currentTime) {
        process.setTurnaroundTime(currentTime - process.getArrivalTime());
        process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
        completedProcesses.add(process);
    }

    private void calculateAndPrintResults(List<Process> completedProcesses) {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (Process process : completedProcesses) {
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }

        averageWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        averageTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();
    }
}