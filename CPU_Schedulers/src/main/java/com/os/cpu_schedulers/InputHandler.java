package com.os.cpu_schedulers;

import java.util.*;
public class InputHandler {
    public static List<Process> getProcesses() {
        Scanner scanner = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();

        System.out.println("Enter the number of processes:");
        int numProcesses = scanner.nextInt();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Enter details for Process " + (i + 1) + " (Name, Color, Arrival Time, Burst Time, Priority):");
            String name = scanner.next();
            String color = scanner.next();
            int arrivalTime = scanner.nextInt();
            int burstTime = scanner.nextInt();
            int priority = scanner.nextInt();
            processes.add(new Process(name, color, arrivalTime, burstTime, priority));
        }

        return processes;
    }
}
