package com.os.cpu_schedulers.Schedulers;

import com.os.cpu_schedulers.ChooseSchedularGUI;
import com.os.cpu_schedulers.Process;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FCAISchedulerGUI extends JFrame {
    private FCAIScheduler fcaiScheduler;

    public FCAISchedulerGUI(ArrayList<Process> processList) {
        setTitle("FCAI Scheduler Results");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Instantiate FCAIScheduler and execute the schedule
        fcaiScheduler = new FCAIScheduler();
        fcaiScheduler.schedule(processList);

        // Create panels
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(resultsPanel), BorderLayout.EAST);

        JPanel ganttChartPanel = new JPanel();
        ganttChartPanel.setLayout(new BorderLayout());
        add(ganttChartPanel, BorderLayout.CENTER);

        // Add Gantt chart
        GanttChartPanel chartPanel = new GanttChartPanel(processList);
        chartPanel.setPreferredSize(new Dimension(700, 200));
        ganttChartPanel.add(chartPanel, BorderLayout.CENTER);

        // Add process details
        JLabel processDetailsLabel = new JLabel("Process Details:");
        processDetailsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultsPanel.add(processDetailsLabel);

        JTextArea processDetailsArea = new JTextArea();
        processDetailsArea.setEditable(false);
        StringBuilder processDetails = new StringBuilder();
        for (Process process : processList) {
            processDetails.append(String.format("Process %s - Waiting Time: %d, Turnaround Time: %d%n",
                    process.getName(), process.getWaitingTime(), process.getTurnaroundTime()));
        }
        processDetailsArea.setText(processDetails.toString());
        resultsPanel.add(new JScrollPane(processDetailsArea));

        // Back button
        JButton backButton = new JButton("Back to Scheduler Selection");
        backButton.addActionListener(e -> {
            new ChooseSchedularGUI(processList, 0); // Replace `0` with actual context switch time if needed
            dispose();
        });
        resultsPanel.add(backButton);

        setVisible(true);
    }

    class GanttChartPanel extends JPanel {
        private List<Process> processes;

        public GanttChartPanel(List<Process> processes) {
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

            // Calculate total time for scaling
            int totalTime = 0;
            for (Process process : processes) {
                totalTime += process.getBurstTime(); // Sum of all burst times
            }

            int currentProcessTime = 0;
            for (Process process : processes) {
                int burstTime = process.getBurstTime();
                int processWidth = (int) ((double) burstTime / totalTime * chartWidth);

                // Draw process bar
                g.setColor(process.getColor());
                g.fillRect(currentProcessTime, yPosition, processWidth, height);

                // Outline of the process bar
                g.setColor(Color.BLACK);
                g.drawRect(currentProcessTime, yPosition, processWidth, height);

                // Draw process name
                g.drawString(process.getName(), currentProcessTime + 5, yPosition + 20);

                // Draw burst time
                g.drawString("T: " + burstTime, currentProcessTime + 5, yPosition + 40);

                currentProcessTime += processWidth; // Move to the next position for the next process
            }

            // Draw the final time marker (end of the last process)
            g.drawString(String.valueOf(totalTime), currentProcessTime - 10, yPosition + 70);
        }
    }
}
