package com.os.cpu_schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FCAISchedulerGUI extends JFrame {
    private ArrayList<Process> processList;
    private int contextSwitch;
    private JTextField v1Field, v2Field;

    public FCAISchedulerGUI(ArrayList<Process> processList) {
        this.processList = processList;
        this.contextSwitch = 0;

        setTitle("FCAI Scheduler");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("FCAI Scheduler", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(15));

        v1Field = createLabeledTextField(mainPanel, "Enter V1 Value:");
        v2Field = createLabeledTextField(mainPanel, "Enter V2 Value:");

        mainPanel.add(Box.createVerticalStrut(15));

        JButton scheduleButton = new JButton("Start FCAI Scheduling");
        scheduleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scheduleButton.setBackground(new Color(0, 123, 255));
        scheduleButton.setForeground(Color.WHITE);
        mainPanel.add(scheduleButton);

        scheduleButton.addActionListener(e -> {
            try {
                double v1 = Double.parseDouble(v1Field.getText().trim());
                double v2 = Double.parseDouble(v2Field.getText().trim());

                // Call FCAI Scheduler
                FCAIScheduler fcaiScheduler = new FCAIScheduler(processList, v1, v2);
                fcaiScheduler.schedule(); // Implement scheduling logic in FCAIScheduler class

                // Optionally show results in a new GUI
                new ScheduleResultGUI(fcaiScheduler.getResults());
                this.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid V1 and V2 values.");
            }
        });

        setVisible(true);
    }

    private JTextField createLabeledTextField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);

        JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(300, 30));
        panel.add(textField);

        return textField;
    }
}
