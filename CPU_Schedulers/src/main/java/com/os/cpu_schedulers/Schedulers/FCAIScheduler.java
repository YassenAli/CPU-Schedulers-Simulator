package com.os.cpu_schedulers.Schedulers;

import java.util.*;
import com.os.cpu_schedulers.Process;



public class FCAIScheduler implements Scheduler {
    private List<Process> processes;
    private double V1, V2;
    private Map<String, List<Integer>> quantumHistory; // Stores quantum updates for each process
    private double averageWaitingTime;
    private double averageTurnaroundTime;
    private List<Process> readyQueue;
    private List<String> executionOrder;

    public FCAIScheduler() {
        readyQueue = new ArrayList<>();
        executionOrder = new ArrayList<>();
        quantumHistory = new HashMap<>();
    }

    @Override
    public void schedule(List<Process> givenProcesses) {
        initializeProcesses(givenProcesses);
        calculateV1AndV2();

        int curTime = 0;
        int totalProcesses = processes.size();
        Set<Process> completedProcesses = new HashSet<>();
        int processIndex = 0;
        int execTime = 0;
        Process curProcess = processes.getFirst();


        while (completedProcesses.size() < totalProcesses) {
            List<Process> newProcesses = new ArrayList<>();
            for (Process process : processes) {
                if (process.getArrivalTime() > curTime) break;
                process.calculateFCAIFactor(V1, V2);
                newProcesses.add(process);
                processIndex++;
            }
            newProcesses.sort(Comparator.comparingInt((Process p) -> p.getArrivalTime()).thenComparingDouble(p -> p.getFCAIFactor()));
            readyQueue.addAll(newProcesses);

            if(readyQueue.isEmpty() && processIndex == processes.size()) {
                break;
            } else if (readyQueue.isEmpty()) {
                System.out.printf("Idle from time %d to %d\n", curTime, curTime + 1);
                curTime++;
                continue;
            }

            if (execTime != 0) {
                Process first = readyQueue.getFirst();
                curProcess = readyQueue.stream()
                        .min(Comparator.comparingDouble(Process::getFCAIFactor))
                        .orElse(null);
                if (curProcess != first) {
                    curProcess.calculateFCAIFactor(V1, V2);
                    int unusedTime = first.getQuantumTime() - execTime;
                    first.setQuantumTime(first.getQuantumTime() + unusedTime);
                    System.out.printf("Time %d: Context switching from Process %s to Process %s\n", curTime, first.getName(), curProcess.getName());
                    readyQueue.removeFirst();
                    readyQueue.remove(curProcess);
                    readyQueue.add(first);
                    readyQueue.addFirst(curProcess);
                    execTime = 0;
                }

            }
            if (execTime == 0) {
                if (curProcess.getRemainingTime() == 0) {
                    if (!completedProcesses.contains(curProcess)) {
                        System.out.printf("Time %d: Context switching from Process %s to Process %s\n", curTime, curProcess.getName(), readyQueue.getFirst().getName());
                        finalizeProcess(curProcess, curTime);
                        completedProcesses.add(curProcess);
                        newProcesses.remove(curProcess);
                        processes.remove(curProcess);
                    }

                }
                curProcess = readyQueue.getFirst();
                System.out.printf("Time %d: Process %s with remaining time %d is executing\n", curTime, curProcess.getName(), curProcess.getRemainingTime());
                execTime = (int) Math.ceil(0.4 * curProcess.getQuantumTime());
                execTime = Math.min(execTime, curProcess.getRemainingTime());
//                curProcess.setQuantumTime(curProcess.getQuantumTime() - execTime);
                curProcess.setRemainingTime(curProcess.getRemainingTime() - execTime);
                curProcess.setHasExecuted40(true);
                curProcess.setStartTime(curTime);
                curTime += execTime;
            } else if (execTime < curProcess.getQuantumTime()) {
                execTime++;
                curTime++;
                curProcess.setRemainingTime(curProcess.getRemainingTime() - 1);
//                curProcess.setQuantumTime(curProcess.getQuantumTime()-1);
            } else {
                curProcess.calculateFCAIFactor(V1, V2);
                curProcess.setQuantumTime(curProcess.getQuantumTime() + 2);
                readyQueue.add(readyQueue.getFirst());
                readyQueue.removeFirst();
                System.out.printf("Time %d: Context switching from Process %s to Process %s\n", curTime, curProcess.getName(), readyQueue.getFirst().getName());
                execTime = 0;
            }

            if (curProcess.getRemainingTime() == 0) {
                readyQueue.removeFirst();
                execTime = 0;
                if (!completedProcesses.contains(curProcess)) {
                    System.out.printf("Time %d: Process %s has completed execution\n", curTime, curProcess.getName());
                    finalizeProcess(curProcess, curTime);
                    completedProcesses.add(curProcess);
                    newProcesses.remove(curProcess);
                    processes.remove(curProcess);
                }

            }
        }
    }
    private void initializeProcesses(List<Process> processes) {
        this.processes = new ArrayList<>(processes);
        this.processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        for (Process process : this.processes) {
            process.setHasExecuted40(false);
            quantumHistory.put(process.getName(), new ArrayList<>(List.of(process.getQuantumTime())));
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
    private void finalizeProcess(Process process, int currentTime) {
        process.setCompletionTime(currentTime);
        process.setTurnaroundTime(currentTime - process.getArrivalTime());
        process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
        executionOrder.add(process.getName() + " completed");
    }

    private void finalizeProcess(Process process, List<Process> completedProcesses, int currentTime) {
        process.setCompletionTime(currentTime);
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
public void schedule(List<Process> processes){
        initializeProcesses(processes);
        calculateV1AndV2();

        int curTime = 0;
        int execTime = 0;

        while(!processes.isEmpty() || !readyQueue.isEmpty()){
            boolean preempt = false;

            for(Iterator<Process> itr = processes.iterator(); itr.hasNext(); ){
                Process p = itr.next();
                if(p.getArrivalTime() <= curTime){
                    readyQueue.add(p);
                    itr.remove();
                }
            }
            if (readyQueue.isEmpty()){
                curTime++;
                continue;
            }
            Process curProcess = readyQueue.poll();
            if (curProcess.getRemainingTime() == curProcess.getBurstTime()){
                curProcess.setStartTime(curTime);
                execTime = (int) Math.ceil(0.4 * curProcess.getQuantumTime());
                curTime += execTime;
                curProcess.setRemainingTime(curProcess.getRemainingTime()-execTime);
                curProcess.setHasExecuted40(true);
                readyQueue.remove(curProcess);

                if (curProcess.getRemainingTime() > 0) {
                    readyQueue.add(curProcess);
//                    curTime++;
                    for(Iterator<Process> itr = processes.iterator(); itr.hasNext(); ){
                        Process p = itr.next();
                        if (p == curProcess) itr.remove();// continue
                        else if(p.getArrivalTime() <= curTime){
                            readyQueue.add(p);
                            if(p.calculateFCAIFactor(V1, V2) < curProcess.getFCAIFactor()){
                                preempt = true;
                                curProcess.calculateFCAIFactor(V1, V2);
                                updateQuantum(curProcess, execTime);
                                execTime = 0;
                                curProcess = p;
                                break;
                            }
                            itr.remove();
                        }
                    }
                    if (!preempt){
                        curTime++;
                        curProcess.setRemainingTime(curProcess.getRemainingTime() - 1);
                        continue;
                    } else{

                    }
                } else{
                    execTime = 0;
                    finalizeProcess(curProcess, curTime);
                    readyQueue.remove(curProcess);
                    continue;
                }
            }
            if (curProcess.isHasExecuted40() && curProcess.getRemainingTime() > 0){
                curTime++;
                execTime++;
                curProcess.setRemainingTime(curProcess.getRemainingTime() - 1);
                for(Iterator<Process> itr = processes.iterator(); itr.hasNext(); ){
                    Process p = itr.next();
                    if(p.getArrivalTime() <= curTime){
                        readyQueue.add(p);
                        itr.remove();
                    }
                }
            }
            if(curProcess.getRemainingTime() <= 0){
                execTime = 0;
                finalizeProcess(curProcess, curTime);
                readyQueue.remove(curProcess);
            }
        }

    }
public void scheduler(List<Process> processes) {
    initializeProcesses(processes);
    calculateV1AndV2();

    int currentTime = 0;

    List<Process> completedProcesses = new ArrayList<>();

    while (!processes.isEmpty() || !readyQueue.isEmpty()) {
        // Add arriving processes to the ready queue
        System.out.println("cur time: "+ currentTime+"=========================");
        for (Iterator<Process> it = processes.iterator(); it.hasNext(); ) {
            Process process = it.next();
            if (process.getArrivalTime() <= currentTime) {
                updateFCAIFactor(process);
                readyQueue.add(process);
                System.out.println("process added: "+process.getName());
                it.remove();
            }
        }
//            if (currentProcess == null && !readyQueue.isEmpty()) {
//                currentProcess = readyQueue.poll();
//                processDetails.get(currentProcess).put("Execution Time", 0); // Reset execution time
//            }
        if (readyQueue.isEmpty()) {
            System.out.printf("Idle from time %d to %d\n", currentTime, currentTime + 1);
            currentTime++;
            continue;
        }


        // Select the process with the best FCAI Factor
        Process currentProcess = selectBestProcess();
        readyQueue.remove(currentProcess);
        System.out.println("process removed: "+ currentProcess.getName());
        int executionTime = 0;
        if (!hasExecuted40Percent.get(currentProcess.getName())) {
            // Execute non-preemptively for 40% of quantum
            executionTime = (int) Math.ceil(0.4 * currentProcess.getQuantumTime());
            executionTime = Math.min(executionTime, currentProcess.getRemainingTime());
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - executionTime);
            currentTime += executionTime;
            executionOrder.add(currentProcess.getName());

            // Mark process as having executed 40%
            hasExecuted40Percent.put(currentProcess.getName(), true);

            // Check if the process is complete
            if (currentProcess.getRemainingTime() == 0) {
                finalizeProcess(currentProcess, completedProcesses, currentTime);
            }
//                else {
//                    readyQueue.add(currentProcess);
//                }
        } else {
            // Process has already executed 40% of quantum
            boolean preempted = false;

            while (!preempted && currentProcess.getRemainingTime() > 0) {
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                executionOrder.add(currentProcess.getName());
                currentTime++;


                // Check for newly arrived processes
                for (Iterator<Process> it = processes.iterator(); it.hasNext(); ) {
                    Process process = it.next();
                    if (process.getArrivalTime() == currentTime) {
                        calculateAndStoreFCAIFactor(process);
                        it.remove();

                        // Preempt if the new process has a better FCAI Factor
                        Process bestProcess = selectBestProcess();
                        if (fcaiFactors.get(bestProcess.getName()) < fcaiFactors.get(currentProcess.getName())) {
                            updateQuantum(currentProcess, executionTime);
                            calculateAndStoreFCAIFactor(currentProcess);
                            currentProcess = bestProcess;
                            readyQueue.add(currentProcess);
                            System.out.println("process added to queue(newly arrived):"+process.getName());
                            preempted = true;
                            break;
                        }
                    }
                }
            }

            // After execution, update the quantum only at the end
            if (currentProcess.getRemainingTime() > 0) {
                readyQueue.add(currentProcess);
            } else if (currentProcess.getRemainingTime() == 0) {
                finalizeProcess(currentProcess, completedProcesses, currentTime);
            }
        }

        System.out.println("finish--------------\n");
    }
//        calculateAndPrintResults(completedProcesses);
}
 */
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