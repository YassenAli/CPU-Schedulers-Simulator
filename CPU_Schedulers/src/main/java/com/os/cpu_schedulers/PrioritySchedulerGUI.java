package com.os.cpu_schedulers;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PrioritySchedulerGUI extends JFrame {
    private ArrayList<Process> processList;
    private static int contextSwitch;
    private Set<Process> processedSet;

    public PrioritySchedulerGUI(ArrayList<Process> processList, int contextSwitch) {
        this.processList = processList;
        processedSet = new HashSet<>();

        // Frame configuration
        setTitle("Priority Scheduler with Gantt Chart");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        Collections.sort(processList, Comparator.comparingInt(Process::getArrivalTime));

        PriorityQueue<Process> waitingProcesses = new PriorityQueue<>(Comparator.comparingInt(Process::getPriority));
        int currentTime = 0;
        int avgWaitingTime = 0;
        int avgTurnaroundTime = 0;

        // To store Gantt chart segments
        ArrayList<GanttSegment> ganttChart = new ArrayList<>();

        for (int i = 0; i < processList.size() || !waitingProcesses.isEmpty(); ) {
            while (i < processList.size() && processList.get(i).getArrivalTime() <= currentTime) {
                Process process = processList.get(i);
                if (!processedSet.contains(process)) {
                    waitingProcesses.add(process);
                    processedSet.add(process);
                }
                i++;
            }

            if (!waitingProcesses.isEmpty()) {
                Process nextProcess = waitingProcesses.poll();

                // Calculate start time and waiting time
                int startTime = Math.max(currentTime, nextProcess.getArrivalTime());
                int waitingTime = startTime - nextProcess.getArrivalTime();
                int turnaroundTime = waitingTime + nextProcess.getBurstTime();

                avgWaitingTime += waitingTime;
                avgTurnaroundTime += turnaroundTime;

                // Add Gantt chart segment
                ganttChart.add(new GanttSegment(nextProcess.getName(), startTime, startTime + nextProcess.getBurstTime(), nextProcess.getColor()));

                // Update currentTime after execution
                currentTime = startTime + nextProcess.getBurstTime();

                // Add context switch if more processes remain
                if (!waitingProcesses.isEmpty() || i < processList.size()) {
                    ganttChart.add(new GanttSegment("CS", currentTime, currentTime + contextSwitch, Color.LIGHT_GRAY));
                    currentTime += contextSwitch;
                }

                // Store waiting and turnaround times in the process object
                nextProcess.setWaitingTime(waitingTime);
                nextProcess.setTurnaroundTime(turnaroundTime);
            } else if (i < processList.size()) {
                // Handle idle time
                int idleTime = processList.get(i).getArrivalTime() - currentTime;
                ganttChart.add(new GanttSegment("Idle", currentTime, currentTime + idleTime, Color.LIGHT_GRAY));
                currentTime = processList.get(i).getArrivalTime();
            }
        }

// Average times
        avgWaitingTime /= processList.size();
        avgTurnaroundTime /= processList.size();


        // GUI components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Gantt chart panel
        JPanel ganttPanel = new GanttChartPanel(ganttChart);
        ganttPanel.setPreferredSize(new Dimension(800, 200));
        mainPanel.add(ganttPanel, BorderLayout.CENTER);

        // Metrics table
        String[] columnNames = {"Process", "Waiting Time", "Turnaround Time"};
        String[][] data = new String[processList.size()][3];
        int index = 0;

        for (Process process : processList) {

            data[index][0] = process.getName();
            data[index][1] = String.valueOf(process.getWaitingTime());
            data[index][2] = String.valueOf(process.getTurnaroundTime());
            index++;
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        // Display averages
        JOptionPane.showMessageDialog(this, "Average Waiting Time: " + avgWaitingTime +
                "\nAverage Turnaround Time: " + avgTurnaroundTime, "Averages", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Example process list
//        ArrayList<Process> processes = new ArrayList<>();
//        processes.add(new Process("P1", Color.RED, 0, 8, 3));
//        processes.add(new Process("P2", Color.BLUE, 2, 5, 1));
//        processes.add(new Process("P3", Color.GREEN, 4, 2, 2));
//
//        int contextSwitchTime = 2;

        SwingUtilities.invokeLater(() -> new PrioritySchedulerGUI(new ArrayList<>() , contextSwitch));
    }

    // Inner class to represent Gantt chart segments
    static class GanttSegment {
        String label;
        int startTime;
        int endTime;
        Color color;

        public GanttSegment(String label, int startTime, int endTime, Color color) {
            this.label = label;
            this.startTime = startTime;
            this.endTime = endTime;
            this.color = color;
        }
    }

    // Panel to draw the Gantt chart
    static class GanttChartPanel extends JPanel {
        private final ArrayList<GanttSegment> segments;

        public GanttChartPanel(ArrayList<GanttSegment> segments) {
            this.segments = segments;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = 50, y = 50, height = 50;

            for (GanttSegment segment : segments) {
                int width = (segment.endTime - segment.startTime) * 20; // Scale each unit time to 20 pixels
                g.setColor(segment.color);
                g.fillRect(x, y, width, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
                g.drawString(segment.label, x + width / 2 - 10, y + height / 2);
                g.drawString(String.valueOf(segment.startTime), x - 5, y + height + 15);
                x += width;
            }
            g.drawString(String.valueOf(segments.get(segments.size() - 1).endTime), x - 5, y + height + 15);
        }
    }
}
