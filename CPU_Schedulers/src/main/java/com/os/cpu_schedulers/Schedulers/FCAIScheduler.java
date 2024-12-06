package com.os.cpu_schedulers.Schedulers;

import java.util.*;
import com.os.cpu_schedulers.Process;



public class FCAIScheduler implements Scheduler {
    private List<Process> processes;
    private double V1, V2;
    private Map<String, List<Integer>> quantumHistory; // Stores quantum updates for each process
    private double averageWaitingTime;
    private double averageTurnaroundTime;
    private PriorityQueue<Process> readyQueue;
    private List<String> executionOrder;
    private Map<String, Double> fcaiFactors;
    private Map<String, Boolean> hasExecuted40Percent;
    private Map<Process, Map<String, Object>> processDetails;

    public FCAIScheduler() {
        readyQueue = new PriorityQueue<>(Comparator.comparingDouble(p -> p.calculateFCAIFactor(V1, V2)));
        executionOrder = new ArrayList<>();
        quantumHistory = new HashMap<>();
    }

    @Override
    public void schedule(List<Process> processes) {
        initializeProcesses(processes);
        calculateV1AndV2();
        CalculateFCAIFactorForEachProcess();

        int curTime = 0;
        int totalProcesses = processes.size();
        int completedProcess = 0;

        while (completedProcess < totalProcesses){
            for (Process process : processes){
                if (process.getArrivalTime() <= curTime && process.getRemainingTime() > 0 && !readyQueue.contains(process)){
                    readyQueue.add(process);
                }
            }
            if(readyQueue.isEmpty()){
                System.out.printf("Idle from time %d to %d\n", curTime, curTime + 1);
                curTime++; continue;
            }
            Process curProcess = readyQueue.poll();
            int execTime = 0;
            if(!curProcess.isHasExecuted40()){
                curProcess.setStartTime(curTime);
                execTime = (int) Math.ceil(0.4 * curProcess.getQuantumTime());
                execTime = Math.min(execTime, curProcess.getRemainingTime());
                curProcess.setQuantumTime(curProcess.getQuantumTime() - execTime);
                curProcess.setRemainingTime(curProcess.getRemainingTime() - execTime);
                curProcess.setHasExecuted40(true);
                curTime += execTime;
                executionOrder.add(curProcess.getName());
            }
            while (curProcess.getRemainingTime() > 0){
                boolean preempted = false;
                curTime++; execTime++;
                curProcess.setQuantumTime(curProcess.getQuantumTime() - 1);
                curProcess.setRemainingTime(curProcess.getRemainingTime() - 1);

                for (Process process : processes){
                    if (process.getArrivalTime() <= curTime && process.calculateFCAIFactor(V1, V2) < curProcess.getFCAIFactor() && process.getRemainingTime() > 0 && !readyQueue.contains(process)){
                        readyQueue.add(process);
                        System.out.printf("%s is interrupted due to %s having a lower FCAI factor.%n", curProcess.getName(), process.getName());
                        preempted = true;
                        updateQuantum(process, execTime);
                        executionOrder.add(curProcess.getName());
                        curProcess.calculateFCAIFactor(V1, V2);
                        break;
                    }
                }
                if(preempted) break;

                execTime++; curTime++;
                curProcess.setQuantumTime(curProcess.getQuantumTime() - 1);
                curProcess.setRemainingTime(curProcess.getRemainingTime() - 1);
                executionOrder.add(curProcess.getName());
                if(curProcess.getRemainingTime() <= 0){
                    finalizeProcess(curProcess, curTime);
                    completedProcess++;
                }
                if(curProcess.getQuantumTime() <= 0){
                    updateQuantum(curProcess, execTime);
                }
            }
            if(curProcess.getRemainingTime() <= 0){
                completedProcess++;
                finalizeProcess(curProcess, curTime);
            }
            if(curProcess.getQuantumTime() <= 0){
                updateQuantum(curProcess, execTime);
            }
        }
    }
    private void initializeProcesses(List<Process> processes) {
        this.processes = new ArrayList<>(processes);
        this.processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        for (Process process : this.processes) {
            process.setHasExecuted40(false);
            process.calculateFCAIFactor(V1, V2);
            quantumHistory.put(process.getName(), new ArrayList<>(List.of(process.getQuantumTime())));
        }
    }
    private void CalculateFCAIFactorForEachProcess(){
        for (Process process : this.processes) {
            process.calculateFCAIFactor(V1, V2);
        }
    }
    private void calculateV1AndV2() {
        double lastArrivalTime = getLastArrivalTime();
        double maxBurstTime = getMaxBurstTime();

        // not Math.ceil
        this.V1 = lastArrivalTime / 10.0;
        this.V2 = maxBurstTime / 10.0;
        System.out.println("V1 and V2: "+V1+" "+V2);
    }

    private void updateFCAIFactor(Process process) {
        double fcaiFactor = (10 - process.getPriority())
                + Math.ceil(process.getArrivalTime() / V1)
                + Math.ceil(process.getRemainingTime() / V2);
        processDetails.get(process).put("FCAI Factor", fcaiFactor);
        fcaiFactors.put(process.getName(), fcaiFactor);
        System.out.println("FCAI Factor & process Name: "+process.getName()+" "+fcaiFactor);
        System.out.println("process remaining time: "+process.getRemainingTime());
        System.out.println("\n");
    }
    private void calculateAndStoreFCAIFactor(Process process) {
        double fcaiFactor = (10 - process.getPriority())
                + Math.ceil(process.getArrivalTime() / V1)
                + Math.ceil(process.getRemainingTime() / V2);
        fcaiFactors.put(process.getName(), fcaiFactor);
        System.out.println("FCAI Factor & process Name: "+process.getName()+" "+fcaiFactor);
        System.out.println("process remaining time: "+process.getRemainingTime());
        System.out.println("\n");
    }
    private void updateQuantum(Process process) {
        int executionTime = (int) processDetails.get(process).get("Execution Time");
        if (executionTime == process.getQuantumTime() && process.getRemainingTime()>0) {
            process.setQuantumTime(process.getQuantumTime() + 2);
        } else {
            int unusedTime = process.getQuantumTime() - executionTime;
            process.setQuantumTime(process.getQuantumTime() + unusedTime);
        }
    }
    private Process selectBestProcess() {
        for (Process process : readyQueue) {
            // Update FCAI Factor for each process in the ready queue
            calculateAndStoreFCAIFactor(process);
        }
        // Find the process with the minimum FCAI Factor
        System.out.println("minimum FCAI Factor: "+readyQueue.stream()
                .min(Comparator.comparingDouble(p -> fcaiFactors.get(p.getName()))));
        return readyQueue.stream()
                .min(Comparator.comparingDouble(p -> fcaiFactors.get(p.getName())))
                .orElseThrow();
    }
    private double getLastArrivalTime() {
        return processes.stream()
                .mapToInt(Process::getArrivalTime)
                .max()
                .orElse(1);
    }
    private double getMaxBurstTime() {
        return processes.stream()
                .mapToInt(Process::getBurstTime)
                .max()
                .orElse(1);
    }

    private void updateQuantum(Process process,int executionTime) {
        int newQuantum;

        // completed
        if (executionTime == process.getQuantumTime()) {
            newQuantum = process.getQuantumTime() + 2;
        } else {
            // if process is preempted
            int unusedTime = process.getQuantumTime() - executionTime;
            newQuantum = process.getQuantumTime() + unusedTime;
        }
        process.setQuantumTime(newQuantum);
        System.out.println("new quantum of process: "+process.getName()+" "+process.getQuantumTime());
        quantumHistory.get(process.getName()).add(newQuantum);
    }
    private void finalizeProcess(Process process, int currentTime) {
        process.setCompletionTime(currentTime);
        process.setTurnaroundTime(currentTime - process.getArrivalTime());
        process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
        executionOrder.add(process.getName() + " completed");
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

//        printResults();
        System.out.println("\nProcess Details:");
        for (Process process : completedProcesses) {
            System.out.printf("Process %s - Waiting Time: %d, Turnaround Time: %d%n",
                    process.getName(), process.getWaitingTime(), process.getTurnaroundTime());
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", averageWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f\n", averageTurnaroundTime);

    }
    @Override
    public void printResults() {
        System.out.println("\nExecution Order: " + String.join(" -> ", executionOrder));
        System.out.println("\nQuantum History:");
        for (Map.Entry<String, List<Integer>> entry : quantumHistory.entrySet()) {
            System.out.printf("Process %s: %s%n", entry.getKey(), entry.getValue());
        }
        for (Process p : processes){
            System.out.println("Process: "+p.getName()+" "+p.getTurnaroundTime()+" "+p.getWaitingTime()+" "+p.getStartTime()+" "+p.getCompletionTime());
        }
    }
}


/*
*
        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            // Add all processes that have arrived at the current time to the ready queue.
            for (Iterator<Process> itr = processes.iterator(); itr.hasNext(); ) {
                Process p = itr.next();
                if (p.getArrivalTime() <= curTime) {
                    readyQueue.add(p);
                    itr.remove();
                }
            }

            if (readyQueue.isEmpty()) {
                curTime++;
                continue; // Move to the next time unit if no process is ready.
            }

            // Get the process with the lowest FCAI Factor.
            Process curProcess = readyQueue.poll();

            if (!curProcess.isHasExecuted40()) {
                // Execute the process for 40% of its quantum time.
                int execTime = (int) Math.ceil(0.4 * curProcess.getQuantumTime());
                execTime = Math.min(execTime, curProcess.getRemainingTime());
                curProcess.setRemainingTime(curProcess.getRemainingTime() - execTime);
                curProcess.setHasExecuted40(true);
                curTime += execTime;

                // Add newly arrived processes during this execution period.
                for (Iterator<Process> itr = processes.iterator(); itr.hasNext(); ) {
                    Process p = itr.next();
                    if (p.getArrivalTime() <= curTime) {
                        readyQueue.add(p);
                        itr.remove();
                    }
                }

                // Check for preemption.
                if (!readyQueue.isEmpty() && readyQueue.peek().calculateFCAIFactor(V1, V2) < curProcess.calculateFCAIFactor(V1, V2)) {
                    readyQueue.add(curProcess); // Re-add current process to the queue.
                    continue; // Preempt current process.
                }

            } else {
                // Execute the remaining burst time for the process or until preemption.
                while (curProcess.getRemainingTime() > 0) {
                    curProcess.setRemainingTime(curProcess.getRemainingTime() - 1);
                    curTime++;

                    // Add newly arrived processes.
                    for (Iterator<Process> itr = processes.iterator(); itr.hasNext(); ) {
                        Process p = itr.next();
                        if (p.getArrivalTime() <= curTime) {
                            readyQueue.add(p);
                            itr.remove();
                        }
                    }

                    // Check for preemption.
                    if (!readyQueue.isEmpty() && readyQueue.peek().calculateFCAIFactor(V1, V2) < curProcess.calculateFCAIFactor(V1, V2)) {
                        readyQueue.add(curProcess); // Re-add current process to the queue.
                        break; // Preempt current process.
                    }
                }
            }

            // If the process has completed, finalize it.
            if (curProcess.getRemainingTime() == 0) {
                finalizeProcess(curProcess, curTime);
            } else {
                // If the process was preempted, re-add it to the queue.
                readyQueue.add(curProcess);
            }
        }
* */



//            // Execute non-preemptively for 40% of quantum time
//            int executionTime = (int) Math.ceil(0.4 * currentProcess.getQuantumTime());
//            executionTime = Math.min(executionTime, currentProcess.getRemainingTime());
//            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - executionTime);
//            currentTime += executionTime;
//            executionOrder.add(currentProcess.getName());
//            System.out.println("process removed: "+ currentProcess.getName());
//
//
//            if (currentProcess.getRemainingTime() > 0) {
//                // If preemption occurs or quantum time completed, recalculate quantum and re-add to the queue
//                updateQuantum(currentProcess, executionTime);
//                calculateAndStoreFCAIFactor(currentProcess);
//                readyQueue.add(currentProcess);
//                System.out.println("process added to queue to complete its execution: "+currentProcess.getName());
//            } else {
//                // Process completes execution
//                finalizeProcess(currentProcess, completedProcesses, currentTime);
//            }