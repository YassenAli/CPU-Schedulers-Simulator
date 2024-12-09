package com.os.cpu_schedulers.Schedulers;

import java.util.Map;
import com.os.cpu_schedulers.Process;

public class ExecutionEntry {
    private Process process;
    private Map<Integer, Integer> details;

    public ExecutionEntry(Process process, Map<Integer, Integer> details) {
        this.process = process;
        this.details = details;
    }

    public Process getProcess() {
        return process;
    }

    public Map<Integer, Integer> getDetails() {
        return details;
    }
}
