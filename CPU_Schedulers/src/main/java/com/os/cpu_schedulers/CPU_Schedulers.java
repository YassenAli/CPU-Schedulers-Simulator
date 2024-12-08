package com.os.cpu_schedulers;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.os.cpu_schedulers.Schedulers.FCAIScheduler;
import com.os.cpu_schedulers.Schedulers.SJFScheduler;
import com.os.cpu_schedulers.Schedulers.SRTFScheduler;
import com.os.cpu_schedulers.Schedulers.SRTFSchedulerGUI;
import com.os.cpu_schedulers.Schedulers.Scheduler;

public class CPU_Schedulers {

    public static void main(String[] args) {
        System.out.println("Welcome to the CPU Scheduler Simulator!");
        List<Process> processes = new ArrayList<>();

        System.out.println("Choose input method:");
        System.out.println("1. Enter data manually");
        System.out.println("2. Read data from a file");
        System.out.println("3. Example in Assignment pdf.");

        Scanner scanner = new Scanner(System.in);
        int inputChoice = scanner.nextInt();

        if (inputChoice == 1) {
            processes = InputHandler.getProcesses();
        } else if (inputChoice == 2) {
            processes = readProcessesFromFile();
        } else if (inputChoice == 3) {
            processes.add(new Process("P1", Color.RED, 0, 17, 4, 4));
            processes.add(new Process("P2", Color.BLUE, 3, 6, 9, 3));
            processes.add(new Process("P3", Color.GREEN, 4, 10, 3, 5));
            processes.add(new Process("P4", Color.YELLOW, 29, 4, 10, 2));
        } else {
            throw new IllegalArgumentException("Invalid choice for input method.");
        }

        System.out.println("\nSelect a Scheduler:");
        System.out.println("1. Priority Scheduling");
        System.out.println("2. Shortest Job First (SJF)");
        System.out.println("3. Shortest Remaining Time First (SRTF)");
        System.out.println("4. FCAI Scheduling");

        int choice = scanner.nextInt();
        Scheduler scheduler;

        switch (choice) {
            // case 1 -> scheduler = new PriorityScheduler(); // Uncomment and implement if
            // needed
            case 2 -> {
                scheduler = new SJFScheduler(processes);
                // scheduler.schedule(processes);
                scheduler.printResults();
                // Launch SJF GUI
                new SJFSchedulerGUI(new ArrayList<>(processes));
            }
            case 3 -> {
                scheduler = new SRTFScheduler(processes);
                // scheduler.schedule(processes);
                scheduler.printResults();
                // Launch SRTF GUI
                new SRTFSchedulerGUI(new ArrayList<>(processes));
            }
            case 4 -> {
                scheduler = new FCAIScheduler();
                scheduler.schedule(processes);
                scheduler.printResults();
            }
            default -> throw new IllegalArgumentException("Invalid choice");
        }
    }

    private static List<Process> readProcessesFromFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file path:");
        String filePath = scanner.next();

        List<Process> processes = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(","); // Assuming CSV format: name,color,arrivalTime,burstTime,priority

                String name = parts[0];
                String colorHex = parts[1];
                int arrivalTime = Integer.parseInt(parts[2]);
                int burstTime = Integer.parseInt(parts[3]);
                int priority = Integer.parseInt(parts[4]);

                Process process = new Process(name, Color.decode(colorHex), arrivalTime, burstTime, priority, 0, 0);
                processes.add(process);
            }

            System.out.println("Processes have been loaded successfully from the file.");
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }

        return processes;
    }
}
