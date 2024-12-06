package com.os.cpu_schedulers;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.*;

public class PrioritySchedulerGUI extends JFrame {

    private ArrayList<Process> processList;
    private static int contextSwitch;
    private Set<Process> processedSet;

    public PrioritySchedulerGUI(ArrayList<Process> processList , int contextSwitch) {
        this.processList = processList;
        processedSet = new HashSet<>();

        // Frame configuration
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Panel configuration
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // kol element hydaf hyt7to vertical
        add(panel, BorderLayout.CENTER);

        // Label configuration
        JLabel label = new JLabel("Priority Scheduler");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label);

        setVisible(true);

        Collections.sort(processList, Comparator.comparingInt(Process::getArrivalTime));
        PriorityQueue<Process> waitingProcesses = new PriorityQueue<>(Comparator.comparingInt(Process::getPriority));
        int currentTime = 0 ;
        int avgWaitingTime = 0;
        int avgTurnAroundTime = 0;

        for (int i = 0; i < processList.size() || !waitingProcesses.isEmpty(); ) {
            // Add processes to the waiting queue that have arrived and are not yet processed
            while (i < processList.size() && processList.get(i).getArrivalTime() <= currentTime) {
                Process process = processList.get(i);
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

            ArrayList<Process> tempQueue = new ArrayList<>();

            while (!waitingProcesses.isEmpty()) {
                Process process = waitingProcesses.poll();
                process.incrementWaitCount();
                tempQueue.add(process);
            }


            if (!tempQueue.isEmpty()) {
                Process lastProcess = tempQueue.get(tempQueue.size() - 1);
                if (lastProcess.getWaitingTime() >= 5) {
                    lastProcess.increasePriority();
                    lastProcess.resetWaitCount();
                    System.out.println("Aging applied: Process " + lastProcess.getName() + " priority increased to " + lastProcess.getPriority());
                }
            }

            waitingProcesses.addAll(tempQueue);



            if (!waitingProcesses.isEmpty()) { // execute

                Process nextProcess = waitingProcesses.poll();
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

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new PrioritySchedulerGUI(new ArrayList<>() , contextSwitch));
    }
}



// todo ===================================================================
// sort processes by arrival time                                       (done)
// variable current time                                                (done)
// make the current time = curent time + burst time                     (done)
// current tiem = current time + context switch                         (done)
// note that the last process doesn't have a
// context switch because     (done)
// check the process that arrived
// if there a process arrived then take the highest priority
// note that
// gui step



/*


Check if waitingProcesses is not empty:
Execute the process with the highest priority (using poll()).
Update currentTime and add contextSwitch.
If waitingProcesses is empty:
Check the next process from processList.
Update currentTime based on its arrivalTime (handle idle time).
Continuously check for new arrivals and add them to waitingProcesses.
Handle Final Process:

Ensure no contextSwitch is added after the last process.

        */

