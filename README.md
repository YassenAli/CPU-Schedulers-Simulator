# CPU Scheduling Simulator

A **CPU Scheduling Simulator** built in Java, designed to demonstrate and simulate various CPU scheduling algorithms such as Priority Scheduling, Shortest Job First (SJF), Shortest Remaining Time First (SRTF), and custom scheduling algorithms.

## Features

- **Interactive Input**: Provides a user-friendly interface for entering process details like name, color, arrival time, burst time, and priority.
- **Multiple Scheduling Algorithms**:
    - Priority Scheduling
    - Shortest Job First (SJF)
    - Shortest Remaining Time First (SRTF)
    - Custom Scheduling Algorithm (FCAI Scheduler)
- **Context Switching**: Simulates context switching with user-defined delays for enhanced realism.
- **Comprehensive Results**: Outputs scheduling order, waiting times, turnaround times, and Gantt charts.

## Input Details

The program requires the following input parameters for each process:
- **Name**: A unique identifier for the process.
- **Color**: A string representing the process's color (for visual differentiation).
- **Arrival Time**: The time when the process arrives in the ready queue.
- **Burst Time**: The total CPU time required by the process.
- **Priority**: (Optional) A number indicating the process's priority (lower values indicate higher priority).

## Output Details

The simulator provides:
- **Execution Order**: The sequence of process execution.
- **Performance Metrics**:
    - Waiting Time
    - Turnaround Time
    - Average Waiting Time
    - Average Turnaround Time
- **Context Switches**: Number of context switches during execution.

## Supported Scheduling Algorithms

### Priority Scheduling
- Schedules processes based on priority, preempting lower-priority processes when a higher-priority process arrives.

### Shortest Job First (SJF)
- Non-preemptive scheduling algorithm that selects the process with the shortest burst time.

### Shortest Remaining Time First (SRTF)
- A preemptive version of SJF, selecting the process with the shortest remaining burst time.

### FCAI Scheduler
- A custom scheduling algorithm tailored to specific use cases.
