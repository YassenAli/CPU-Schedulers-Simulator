package com.os.cpu_schedulers;

import javax.swing.*;
import java.awt.*;

public class ChooseSchedularGUI extends JFrame {
    private JPanel mainPanel;

    public ChooseSchedularGUI() {
        setTitle("Choose Scheduler GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 500);
        setLocationRelativeTo(null);

        // Main panel with vertical layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(mainPanel, BorderLayout.CENTER);

        // Title Label
        JLabel titleLabel = new JLabel("Choose Scheduling Algorithm", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Add padding to the top of the title
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        mainPanel.add(titleLabel);

        // Add vertical spacing (50 pixels below the label)
        mainPanel.add(Box.createVerticalStrut(50));

        // Priority Scheduler Button
        JButton prioritySchedulerButton = new JButton("Priority Scheduler");
        prioritySchedulerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        prioritySchedulerButton.setPreferredSize(new Dimension(450, 40));
        prioritySchedulerButton.setMaximumSize(prioritySchedulerButton.getPreferredSize());
        prioritySchedulerButton.setFont(new Font("Arial", Font.BOLD, 15));
        prioritySchedulerButton.setBorderPainted(false);
        prioritySchedulerButton.setFocusPainted(false);

        // Add the first button to the panel
        mainPanel.add(prioritySchedulerButton);

        // Add vertical spacing (30 pixels between buttons)
        mainPanel.add(Box.createVerticalStrut(30));

        // SJF Scheduler Button
        JButton SJFSchedulerButton = new JButton("SJF Scheduler");
        SJFSchedulerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        SJFSchedulerButton.setPreferredSize(new Dimension(450, 40));
        SJFSchedulerButton.setMaximumSize(prioritySchedulerButton.getPreferredSize());
        SJFSchedulerButton.setFont(new Font("Arial", Font.BOLD, 15));
        SJFSchedulerButton.setBorderPainted(false);
        SJFSchedulerButton.setFocusPainted(false);

        // Add the second button to the panel
        mainPanel.add(SJFSchedulerButton);


        // Add vertical spacing (30 pixels between buttons)
        mainPanel.add(Box.createVerticalStrut(30));

        // SJF Scheduler Button
        JButton SRTFSchedulerButton = new JButton("SRTF Scheduler");
        SRTFSchedulerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        SRTFSchedulerButton.setPreferredSize(new Dimension(450, 40));
        SRTFSchedulerButton.setMaximumSize(prioritySchedulerButton.getPreferredSize());
        SRTFSchedulerButton.setFont(new Font("Arial", Font.BOLD, 15));
        SRTFSchedulerButton.setBorderPainted(false);
        SRTFSchedulerButton.setFocusPainted(false);

        // Add the second button to the panel
        mainPanel.add(SRTFSchedulerButton);


        // Add vertical spacing (30 pixels between buttons)
        mainPanel.add(Box.createVerticalStrut(30));

        // SJF Scheduler Button
        JButton FCAISchedulerButton = new JButton("FCAI Scheduler");
        FCAISchedulerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        FCAISchedulerButton.setPreferredSize(new Dimension(450, 40));
        FCAISchedulerButton.setMaximumSize(prioritySchedulerButton.getPreferredSize());
        FCAISchedulerButton.setFont(new Font("Arial", Font.BOLD, 15));
        FCAISchedulerButton.setBorderPainted(false);
        FCAISchedulerButton.setFocusPainted(false);

        mainPanel.add(FCAISchedulerButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChooseSchedularGUI::new); // Create and show the GUI
    }
}