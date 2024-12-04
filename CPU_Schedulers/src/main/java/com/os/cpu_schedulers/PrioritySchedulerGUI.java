package com.os.cpu_schedulers;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PrioritySchedulerGUI extends JFrame {

    private ArrayList<Process> processList;

    public PrioritySchedulerGUI(ArrayList<Process> processList) {
        this.processList = processList;

        // Frame configuration
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Panel configuration
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // what this do
        add(panel, BorderLayout.CENTER);

        // Label configuration
        JLabel label = new JLabel("Priority Scheduler");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrioritySchedulerGUI(new ArrayList<>()));
    }
}