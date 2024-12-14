package com.os.cpu_schedulers.Schedulers;

import com.os.cpu_schedulers.ChooseSchedularGUI;
import com.os.cpu_schedulers.Process;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SRTFSchedulerGUI extends JFrame {
    private JTextArea resultArea;
    private ArrayList<Process> processList;

    public SRTFSchedulerGUI(ArrayList<Process> processList) {
        this.processList = processList;

        // Initialize JFrame
        setTitle("SRTF Scheduling Results");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Create a JPanel to hold all components (results and Gantt chart)
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Vertical layout

        // Text area for displaying results
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane textAreaScrollPane = new JScrollPane(resultArea);
        panel.add(textAreaScrollPane);

        // Call the method to schedule and display the results
        scheduleProcesses(panel);

        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back to Schedulers");
        backButton.addActionListener(e -> {
            dispose(); // Close current window
            new ChooseSchedularGUI((ArrayList<Process>) processList, 0);
        });

        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel inside a JScrollPane to make the entire window scrollable
        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void scheduleProcesses(JPanel panel) {
        if (processList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No processes to schedule.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create an SRTF scheduler and schedule processes
        SRTFScheduler srtfScheduler = new SRTFScheduler(processList);
        srtfScheduler.schedule(processList);

        // Retrieve the correct execution order
        ArrayList<Process> executionOrder = new ArrayList<>(srtfScheduler.getExecutionOrder());

        // Display the results
        resultArea.setText("SRTF Scheduling Results:\n");
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
        ganttChartPanel.setPreferredSize(new Dimension(1200, 200)); // Adjust the width as needed
        JScrollPane ganttChartScrollPane = new JScrollPane(ganttChartPanel);
        panel.add(ganttChartScrollPane); // Add the Gantt chart scroll pane to the main panel

        // Refresh the layout
        validate();
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

            int currentTime = 0; // Start time for the chart
            int height = 50; // Height of each process bar
            int yPosition = 50; // Vertical position of the bar
            int chartWidth = getWidth() - 40; // Leave some padding on the sides

            // Find the total time (for scaling purposes)
            int totalBurstTime = processes.stream().mapToInt(Process::getBurstTime).sum();
            int cumulativeTime = 0;

            for (Process process : processes) {
                int burstTime = process.getBurstTime();
                int processWidth = (int) ((double) burstTime / totalBurstTime * chartWidth);

                // Draw the process bar
                g.setColor(process.getColor());
                g.fillRect(currentTime, yPosition, processWidth, height);

                // Outline of the process bar
                g.setColor(Color.BLACK);
                g.drawRect(currentTime, yPosition, processWidth, height);

                // Draw the process name
                g.drawString(process.getName(), currentTime + 5, yPosition + 20); // Process name
                g.drawString("T: " + burstTime, currentTime + 5, yPosition + 40); // Burst time

                // Draw the cumulative time
                cumulativeTime += burstTime;
                g.drawString("Total Time: " + cumulativeTime, currentTime + (processWidth / 2), yPosition + 70);

                currentTime += processWidth;
            }
        }
    }

    public static void main(String[] args) {
        // Add sample processes for testing
        ArrayList<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", Color.RED, 0, 17, 4, 4, 0));
        processes.add(new Process("P2", Color.BLUE, 3, 6, 9, 3, 0));
        processes.add(new Process("P3", Color.GREEN, 4, 10, 3, 5, 0));
        processes.add(new Process("P4", Color.YELLOW, 29, 4, 10, 2, 0));

        new SRTFSchedulerGUI(processes);
    }
}
