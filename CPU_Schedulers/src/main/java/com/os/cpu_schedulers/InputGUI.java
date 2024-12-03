package com.os.cpu_schedulers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class InputGUI extends JFrame {
    private int totalProcesses; // Total number of processes
    private int currentProcessIndex; // Index of the current process being entered
    private JPanel mainPanel; // Main panel for dynamic content
    private JTextField processNameField;
    private JTextField processColorField;
    private JTextField arrivalTimeField;
    private JTextField burstTimeField;
    private JTextField priorityField;
    private ArrayList<Process> processList;
    private JPanel panel1;

    public InputGUI() {
        processList = new ArrayList<>(); // Initialize the list

        setTitle("CPU Scheduler Input");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Initialize main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(mainPanel, BorderLayout.CENTER);

        // Show initial form for entering the number of processes
        showInitialForm();

        setVisible(true);
    }

    private void showInitialForm() {
        mainPanel.removeAll();

        // Add padding around the panel using an empty border
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("CPU Scheduler", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add vertical spacing

        JLabel numProcessesLabel = new JLabel("Enter the number of processes:");
        numProcessesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(numProcessesLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing

        JTextField numProcessesField = new JTextField();
        numProcessesField.setMaximumSize(new Dimension(200, 30));
        mainPanel.add(numProcessesField);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add vertical spacing

        JButton submitButton = new JButton("Submit");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(submitButton);

        submitButton.addActionListener(e -> {
            try {
                totalProcesses = Integer.parseInt(numProcessesField.getText().trim());
                if (totalProcesses < 1) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive number.");
                } else {
                    currentProcessIndex = 1;
                    showProcessForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    private void showProcessForm() {
        mainPanel.removeAll();

        JLabel titleLabel = new JLabel("Enter Details for Process " + currentProcessIndex, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel);

        processNameField = createLabeledTextField("Process Name:");
        processColorField = createLabeledTextField("Process Color:");
        arrivalTimeField = createLabeledTextField("Arrival Time:");
        burstTimeField = createLabeledTextField("Burst Time:");
        priorityField = createLabeledTextField("Priority:");

        JButton nextButton = new JButton(currentProcessIndex == totalProcesses ? "Finish" : "Next");
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.addActionListener(e -> handleNextButton());
        mainPanel.add(nextButton);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JTextField createLabeledTextField(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(label);

        JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(300, 30));
        mainPanel.add(textField);

        return textField;
    }

    private void handleNextButton() {
        String processName = processNameField.getText().trim();
        String processColor = processColorField.getText().trim();
        int arrivalTime = Integer.parseInt(arrivalTimeField.getText().trim());
        int burstTime = Integer.parseInt(burstTimeField.getText().trim());
        int priority = Integer.parseInt(priorityField.getText().trim());

//        if (processName.isEmpty() || processColor.isEmpty() || arrivalTime.isEmpty() ||
//                burstTime.isEmpty() || priority.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
//            return;
//        }


        // Create a new Process object and add it to the list
        Process process = new Process(processName, processColor, arrivalTime, burstTime, priority);
        processList.add(process);

        // Process the entered details here (e.g., save to a list or database)
//        System.out.println("Process " + currentProcessIndex + ":");
//        System.out.println("Name: " + processName);
//        System.out.println("Color: " + processColor);
//        System.out.println("Arrival Time: " + arrivalTime);
//        System.out.println("Burst Time: " + burstTime);
//        System.out.println("Priority: " + priority);

        // Move to the next process or finish
        if (currentProcessIndex < totalProcesses) {
            currentProcessIndex++;
            showProcessForm();
        } else {
            JOptionPane.showMessageDialog(this, "All processes entered successfully!");
            for(int i = 0 ; i < totalProcesses ; i++) {
                System.out.println(processList.get(i));
            }
            dispose(); // Close the application or redirect as needed
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InputGUI::new);
    }
}
