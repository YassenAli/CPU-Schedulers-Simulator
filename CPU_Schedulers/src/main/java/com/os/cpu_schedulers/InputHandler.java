package com.os.cpu_schedulers;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class InputHandler extends JFrame {
    public static List<Process> getProcesses() {
        Scanner scanner = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();

        System.out.println("Enter the number of processes:");
        int numProcesses = scanner.nextInt();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Enter details for Process " + (i + 1) + ":");

            System.out.print("Name: ");
            String name = scanner.next();

            System.out.print("Color: ");
//            Color color = Color.decode(scanner.next());

            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();

            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            System.out.print("Priority (lower value = higher priority): ");
            int priority = scanner.nextInt();

            System.out.print("Quantum Time: ");
            int quantum = scanner.nextInt();

            Process process = new Process(name, Color.red, arrivalTime, burstTime, priority, quantum);
            processes.add(process);
        }

        System.out.println("\nProcesses have been initialized successfully.\n");
        return processes;
    }
}
