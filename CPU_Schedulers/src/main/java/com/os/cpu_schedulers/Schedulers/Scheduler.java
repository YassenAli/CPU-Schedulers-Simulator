package com.os.cpu_schedulers.Schedulers;


import java.util.List;

public interface Scheduler {
	void schedule(List<Process> processes);
	void printResults();
}
