package com.os.cpu_schedulers.Schedulers;

import com.os.cpu_schedulers.ChooseSchedularGUI;
import com.os.cpu_schedulers.Process;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FCAISchedulerGUI extends JFrame {
    private FCAIScheduler scheduler;
    private ArrayList<Process> processList;

    public FCAISchedulerGUI(ArrayList<Process> processList) {
        this.processList = processList;
        this.scheduler = new FCAIScheduler();

        List<Process> givenProcess = new ArrayList<>(processList);

        // Schedule processes
        scheduler.schedule(givenProcess);

        // Frame configuration
        setTitle("FCAI Scheduler with Gantt Chart");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Add Gantt chart panel
        JPanel ganttPanel = createGanttChartPanel(scheduler.getExecutionOrder(), processList);
        ganttPanel.setPreferredSize(new Dimension(800, 200));
        mainPanel.add(ganttPanel, BorderLayout.NORTH);

        // Add process metrics table
        JTable table = createMetricsTable(processList);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Quantum history table
        JTable quantumTable = createQuantumHistoryTable(scheduler);
        JScrollPane quantumScrollPane = new JScrollPane(quantumTable);
        mainPanel.add(quantumScrollPane, BorderLayout.SOUTH);

        // Add navigation buttons
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back to Schedulers");
        backButton.addActionListener(e -> dispose()); // Close the window
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        // Show averages
        JOptionPane.showMessageDialog(this,
                "Average Waiting Time: " + scheduler.getAverageWaitingTime() +
                        "\nAverage Turnaround Time: " + scheduler.getAverageTurnaroundTime(),
                "Averages", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createGanttChartPanel(List<String> executionOrder, ArrayList<Process> processes) {
        // Convert execution order to Gantt chart segments
        ArrayList<GanttSegment> segments = new ArrayList<>();
        for (String order : executionOrder) {
            String[] parts = order.split(" ");
            if (parts[1].equals("completed")) {
                continue;
            }
            Process process = processes.stream()
                    .filter(p -> p.getName().equals(parts[1]))
                    .findFirst().orElse(null);
            if (process != null) {
                segments.add(new GanttSegment(process.getName(), process.getArrivalTime(),
                        process.getCompletionTime(), process.getColor()));
            }
        }

        return new GanttChartPanel(segments);
    }

    private JTable createMetricsTable(ArrayList<Process> processes) {
        String[] columnNames = {"Process", "Waiting Time", "Turnaround Time"};
        String[][] data = new String[processes.size()][3];
        int index = 0;

        for (Process process : processes) {
            data[index][0] = process.getName();
            data[index][1] = String.valueOf(process.getWaitingTime());
            data[index][2] = String.valueOf(process.getTurnaroundTime());
            index++;
        }

        return new JTable(data, columnNames);
    }

    private JTable createQuantumHistoryTable(FCAIScheduler scheduler) {
        Map<String, List<Integer>> quantumHistory = scheduler.getQuantumHistory();
        String[] columnNames = {"Process", "Quantum History"};
        String[][] data = new String[quantumHistory.size()][2];
        int index = 0;

        for (Map.Entry<String, List<Integer>> entry : quantumHistory.entrySet()) {
            data[index][0] = entry.getKey();
            data[index][1] = entry.getValue().toString();
            index++;
        }

        return new JTable(data, columnNames);
    }

    public class GanttSegment {
        private String label;
        private int startTime;
        private int endTime;
        private Color color;
        public GanttSegment(String label, int startTime, int endTime, Color color) {
            this.label = label;
            this.startTime = startTime;
            this.endTime = endTime;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public Color getColor() {
            return color;
        }
        public void setColor(Color color) {
            this.color = color;
        }
    }
    static class GanttChartPanel extends JPanel {
        private final ArrayList<GanttSegment> segments;

        public GanttChartPanel(ArrayList<GanttSegment> segments) {
            this.segments = segments;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (segments.isEmpty()) {
                g.setColor(Color.BLACK);
                g.drawString("No data available for Gantt chart", 50, 50);
                return;
            }

            int x = 50, y = 50, height = 50;

            for (GanttSegment segment : segments) {
                int width = (segment.getEndTime() - segment.getStartTime()) * 20; // Scale each unit time to 20 pixels
                g.setColor(segment.getColor());
                g.fillRect(x, y, width, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
                g.drawString(segment.getLabel(), x + width / 2 - 10, y + height / 2);
                g.drawString(String.valueOf(segment.getStartTime()), x - 5, y + height + 15);
                x += width;
            }
            g.drawString(String.valueOf(segments.get(segments.size() - 1).getEndTime()), x - 5, y + height + 15);
        }

    }

    public static void main(String[] args) {
        // Example process list
        ArrayList<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", Color.RED, 0, 17, 4, 4));
        processes.add(new Process("P2", Color.BLUE, 3, 6, 9, 3));
        processes.add(new Process("P3", Color.GREEN, 4, 10, 3, 5));
        processes.add(new Process("P4", Color.YELLOW, 29, 4, 10, 2));

        SwingUtilities.invokeLater(() -> new FCAISchedulerGUI(processes));
    }
}