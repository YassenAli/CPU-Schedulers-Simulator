package com.os.cpu_schedulers;

import com.os.cpu_schedulers.Schedulers.*;

import java.util.List;
import java.util.Scanner;

public class CPU_Schedulers {

    public static void main(String[] args) {
        System.out.println("Welcome to the CPU Scheduler Simulator!");
        List<Process> processes = InputHandler.getProcesses();

        System.out.println("\nSelect a Scheduler:");
        System.out.println("1. Priority Scheduling");
        System.out.println("2. Shortest Job First (SJF)");
        System.out.println("3. Shortest Remaining Time First (SRTF)");
        System.out.println("4. FCAI Scheduling");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        Scheduler scheduler;

        switch (choice) {
//            case 1 -> scheduler = new PriorityScheduler();
//            case 2 -> scheduler = new SJFScheduler();
//            case 3 -> scheduler = new SRTFScheduler();
            case 4 -> scheduler = new FCAIScheduler();
            default -> throw new IllegalArgumentException("Invalid choice");
        }

        scheduler.schedule(processes);
        scheduler.printResults();
    }
}
