package com.os.cpu_schedulers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class InputGUI extends JFrame {
    private int totalProcesses; // Total number of processes
    private int currentProcessIndex; // Index of the current process being entered
    private JPanel mainPanel; // Main panel for dynamic content
    private JTextField processNameField;
    private JPanel processColorPreview; // Panel to preview the selected color
    private Color selectedColor; // Variable to store the selected color
    private JTextField arrivalTimeField;
    private JTextField burstTimeField;
    private JTextField priorityField;
    private JTextField quantumTimeField;
    private ArrayList<Process> processList;
    private int contextSwitch ;
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


        mainPanel.add(Box.createRigidArea(new Dimension(0 , 5)));
        JLabel contextswitchLabel = new JLabel("Enter Context Switch : ");
        contextswitchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(contextswitchLabel);
        JTextField contextSwitchInput = new JTextField();
        contextSwitchInput.setMaximumSize(new Dimension(200, 30));
        mainPanel.add(contextSwitchInput);


        mainPanel.add(Box.createRigidArea(new Dimension(0 , 15)));

        JButton submitButton = new JButton("Submit");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(200, 30));
        submitButton.setBackground(new Color(0 , 0 , 0));
        submitButton.setForeground(Color.WHITE);
        mainPanel.add(submitButton);

        submitButton.addActionListener(e -> {
            try {
                totalProcesses = Integer.parseInt(numProcessesField.getText().trim());
                contextSwitch = Integer.parseInt(contextSwitchInput.getText().trim());
                if (totalProcesses < 1 || contextSwitch < 0) {
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
        setSize(500 , 500);
        JLabel titleLabel = new JLabel("Enter Details for Process " + currentProcessIndex, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        processNameField = createLabeledTextField("Process Name:");

        // Add color picker and preview
        JLabel colorLabel = new JLabel("Pick Process Color:");
        colorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(colorLabel);

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));

        processColorPreview = new JPanel();
        processColorPreview.setPreferredSize(new Dimension(15, 15));
        processColorPreview.setMinimumSize(new Dimension(50, 50));
        processColorPreview.setMaximumSize(new Dimension(50, 50));
        processColorPreview.setBackground(Color.BLACK); // Default color
        selectedColor = Color.BLACK; // Default color

        JButton colorButton = new JButton("Pick Color");
        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(InputGUI.this, "Choose a color", selectedColor);
            if (color != null) {
                selectedColor = color;
                processColorPreview.setBackground(color);
            }
        });

        colorPanel.add(processColorPreview);
        colorPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacing
        colorPanel.add(colorButton);

        mainPanel.add(colorPanel);

        arrivalTimeField = createLabeledTextField("Arrival Time:");
        burstTimeField = createLabeledTextField("Burst Time:");
        priorityField = createLabeledTextField("Priority:");
        quantumTimeField = createLabeledTextField("Quantum Time:");

        JButton nextButton = new JButton(currentProcessIndex == totalProcesses ? "Finish" : "Next");
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.addActionListener(e -> handleNextButton());
        mainPanel.add(Box.createVerticalStrut(20));
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
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(textField);

        return textField;
    }

    private void handleNextButton() {
        String processName = processNameField.getText().trim();
        int arrivalTime = Integer.parseInt(arrivalTimeField.getText().trim());
        int burstTime = Integer.parseInt(burstTimeField.getText().trim());
        int priority = Integer.parseInt(priorityField.getText().trim());
        int quantumTime = Integer.parseInt(quantumTimeField.getText().trim());

        // Create a new Process object and add it to the list
        Process process = new Process(processName, selectedColor, arrivalTime, burstTime, priority , quantumTime , 0);
        processList.add(process);

        System.out.println("Process " + currentProcessIndex + ":");
        System.out.println("Name: " + processName);
        System.out.println("Color: " + selectedColor);
        System.out.println("Arrival Time: " + arrivalTime);
        System.out.println("Burst Time: " + burstTime);
        System.out.println("Priority: " + priority);
        System.out.println("Quantum Time: " + quantumTime);

        // Move to the next process or finish
        if (currentProcessIndex < totalProcesses) {
            currentProcessIndex++;
            showProcessForm();
        } else {
            JOptionPane.showMessageDialog(this, "All processes entered successfully!");
            for (Process p : processList) {
                System.out.println(p);
            }
            dispose(); // Close the application or redirect as needed
            SwingUtilities.invokeLater(() -> new ChooseSchedularGUI(processList , contextSwitch));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InputGUI::new);
    }
}
