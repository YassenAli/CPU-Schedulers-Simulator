package com.os.cpu_schedulers;

import java.awt.*;

public class Process {
    private String name;
    private java.awt.Color color;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int quantumTime;
    private int remainingTime;
    private int startTime = -1;
    private int completionTime;
    private int waitingTime;
    private int turnaroundTime;

    public Process(String name, Color color, int arrivalTime, int burstTime, int priority , int quantumTime) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.quantumTime = quantumTime;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    @Override
    public String toString() {
        return String.format(
                "Process{name='%s', arrivalTime=%d, burstTime=%d, priority=%d, waitingTime=%d, turnaroundTime=%d}",
                name, arrivalTime, burstTime, priority, waitingTime, turnaroundTime);
    }
}
