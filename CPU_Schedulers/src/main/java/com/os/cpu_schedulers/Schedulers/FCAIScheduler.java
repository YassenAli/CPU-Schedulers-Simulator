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
    private Map<Process, Map<String, Object>> processDetails;

    public FCAIScheduler() {
        readyQueue = new PriorityQueue<>(Comparator.comparingDouble(p -> p.calculateFCAIFactor(V1, V2)));
//        processDetails = new HashMap<>();
        executionOrder = new ArrayList<>();
        quantumHistory = new HashMap<>();
//        fcaiFactors = new HashMap<>();
    }

    @Override
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

    private void initializeProcesses(List<Process> processes) {
        this.processes = new ArrayList<>(processes);
        this.processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        for (Process process : this.processes) {
            process.setHasExecuted40(false);
            process.calculateFCAIFactor(V1, V2);
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
    }
}





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