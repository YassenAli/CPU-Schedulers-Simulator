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
    private boolean hasExecuted40;
    private double FCAIFactor;

    public Process(String name, Color color, int arrivalTime, int burstTime, int priority , int quantumTime) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.startTime = burstTime;
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
    public void setBurstTime(int burstTime){ this.burstTime = burstTime; }

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

    public double calculateFCAIFactor(double V1, double V2) {
        this.FCAIFactor = (10 - this.priority) + Math.ceil(this.arrivalTime / V1) + Math.ceil(this.remainingTime / V2);
        return FCAIFactor;
    }

    public double getFCAIFactor() {
        return FCAIFactor;
    }

    public boolean isHasExecuted40() {
        return hasExecuted40;
    }

    public void setHasExecuted40(boolean hasExecuted40) {
        this.hasExecuted40 = hasExecuted40;
    }
    @Override
    public String toString() {
        return String.format(
                "Process{name='%s', arrivalTime=%d, burstTime=%d, priority=%d, waitingTime=%d, turnaroundTime=%d}",
                name, arrivalTime, burstTime, priority, waitingTime, turnaroundTime);
    }

    public int getQuantumTime() {
        return quantumTime;
    }

    public void setQuantumTime(int quantumTime) {
        this.quantumTime = quantumTime;
    }
}
