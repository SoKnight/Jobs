package ru.soknight.jobs.database;

import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.utils.Utils;

public class JobProfile {

	private String name;
	private JobType job;
	private int level = 1, progress = 0, maxlevel;
	
	private double salary;
	
	public JobProfile(String name, JobType job, int level, int progress) {
		this.name = name;
		this.job = job;
		this.maxlevel = DatabaseManager.getJobInfo(job).getMaxLevel();
		this.level = level;
		this.progress = progress;
		refreshSalary();
	}
	
	public JobProfile(String name, JobType job) {
		this.name = name;
		this.job = job;
		this.maxlevel = DatabaseManager.getJobInfo(job).getMaxLevel();
		refreshSalary();
	}
	
	public void refreshSalary() {
		salary = Config.config.getDouble("salaries." + job.name());
	}

	public String getName() {
		return name;
	}

	public JobType getJob() {
		return job;
	}
	
	public void addLevel() {
		level++;
	}

	public int getLevel() {
		return level;
	}
	
	public int addProgress(int exp) {
		int uneeded = Utils.getNeededExp(level + 1);
		int needed = uneeded - progress;
		int nextlevel = level + 1;
		
		if(exp >= needed) {
			if(nextlevel <= maxlevel) {
				level++;
				progress = exp - needed; 
			} else progress = uneeded;
		} else progress += exp;

		if(level == nextlevel) return level;
		else return -1;
	}

	public int getProgress() {
		return progress;
	}
	
	public int getNeeded() {
		return Utils.getNeededExp(level + 1);
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public double getSalary() {
		return salary;
	}
	
}
