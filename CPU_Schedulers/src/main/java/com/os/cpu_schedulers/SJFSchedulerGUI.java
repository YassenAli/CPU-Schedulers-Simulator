package com.os.cpu_schedulers;

import com.os.cpu_schedulers.Process;
import com.os.cpu_schedulers.Schedulers.SJFScheduler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SJFSchedulerGUI extends JFrame {
    private JTextArea resultArea;
    private ArrayList<Process> processList;

    public SJFSchedulerGUI(ArrayList<Process> processList) {
        this.processList = processList; // Receive the process list from InputGUI

        // Initialize JFrame
        setTitle("SJF Scheduling Results");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Text area for displaying results
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.NORTH);

        // Call the method to schedule and display the results
        scheduleProcesses();
        setVisible(true);
    }

    private void scheduleProcesses() {
        if (processList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No processes to schedule.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create an SJF scheduler and schedule processes
        SJFScheduler sjfScheduler = new SJFScheduler(processList);
        sjfScheduler.schedule(processList);

        // Retrieve the correct execution order
        ArrayList<Process> executionOrder = new ArrayList<>(sjfScheduler.getExecutionOrder());

        // Display the results
        resultArea.setText("SJF Scheduling Results:\n");
        resultArea.append("Process\tWaiting Time\tTurnaround Time\n");

        float totalWaitTime = 0;
        float totalTurnaroundTime = 0;

        for (Process process : executionOrder) {
            resultArea.append(process.getName() + "\t" + process.getWaitingTime() + "\t\t" + process.getTurnaroundTime() + "\n");
            totalWaitTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }

        float averageWaitTime = totalWaitTime / executionOrder.size();
        float averageTurnaroundTime = totalTurnaroundTime / executionOrder.size();
        resultArea.append("\nAverage Waiting Time: " + averageWaitTime);
        resultArea.append("\nAverage Turnaround Time: " + averageTurnaroundTime);

        // Add Gantt chart visualization
        GanttChartPanel ganttChartPanel = new GanttChartPanel(executionOrder);
        ganttChartPanel.setPreferredSize(new Dimension(600, 200));
        add(ganttChartPanel, BorderLayout.CENTER);
        validate();
    }

    // Main method to launch the GUI (normally used when integrated with InputGUI)
    public static void main(String[] args) {
        // Add sample processes for testing
        ArrayList<Process> processes = new ArrayList<>();
        new SJFSchedulerGUI(processes);
    }
}

// GanttChartPanel class to visualize the processes in a Gantt chart format
class GanttChartPanel extends JPanel {
    private ArrayList<Process> processes;

    public GanttChartPanel(ArrayList<Process> processes) {
        this.processes = processes;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (processes.isEmpty()) return;

        int currentTime = 20; // Start time for the chart
        int height = 50; // Height of each process bar
        int yPosition = 50; // Vertical position of the bar
        int chartWidth = getWidth() - 40; // Leave some padding on the sides

        // Find the total time (for scaling purposes)
        int totalBurstTime = processes.stream().mapToInt(Process::getBurstTime).sum();

        for (Process process : processes) {
            int burstTime = process.getBurstTime();
            int processWidth = (int) ((double) burstTime / totalBurstTime * chartWidth);

            g.setColor(process.getColor());
            g.fillRect(currentTime, yPosition, processWidth, height);

            g.setColor(Color.BLACK);
            g.drawRect(currentTime, yPosition, processWidth, height); // Outline
            g.drawString(process.getName(), currentTime + 5, yPosition + 20); // Process name
            g.drawString("T: " + burstTime, currentTime + 5, yPosition + 40); // Burst time

            currentTime += processWidth;
        }
    }
}
