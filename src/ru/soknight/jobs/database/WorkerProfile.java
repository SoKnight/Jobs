package ru.soknight.jobs.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerProfile {

	private Map<JobType, JobProfile> jobs = new HashMap<>();
	private List<JobType> jobtypes = new ArrayList<>();
	private JobType current = JobType.JOBLESS;
	private String name;
	
	public WorkerProfile(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public List<JobType> getJobs() {
		return jobtypes;
	}

	public JobType getCurrentJob() {
		return current;
	}

	public void setCurrentJob(JobType job) {
		this.current = job;
	}
	
	public void resetCurrentJob() {
		current = JobType.JOBLESS;
	}
	
	public JobProfile getJobProfile(JobType job) {
		return jobs.get(job);
	}
	
	public boolean hasJobProfile(JobType job) {
		return jobs.containsKey(job);
	}
	
	public void setJobProfile(JobType job, JobProfile profile) {
		jobs.put(job, profile);
		jobtypes.add(job);
	}
	
	public void removeJobProfile(JobType job) {
		jobs.remove(job);
		jobtypes.remove(job);
		if(current.equals(job)) current = JobType.JOBLESS;
	}
	
	public int getJobCount() {
		return jobtypes.size();
	}
	
}
