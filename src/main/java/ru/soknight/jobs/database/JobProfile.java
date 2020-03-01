package ru.soknight.jobs.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.utils.ExpUtils;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "profiles")
public class JobProfile {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String name;
	@DatabaseField
	private JobType job;
	@DatabaseField
	private int level;
	@DatabaseField
	private int progress;
	@DatabaseField(foreign = true)
	private WorkerProfile worker;
	
	public JobProfile(String name, JobType job) {
		this.name = name;
		this.job = job;
		this.level = 1;
		this.progress = 0;
	}
	
	public void addLevel() {
		level++;
	}
	
	public int addProgress(int exp) throws NotLoadedConfigException {
		int uneeded = getNeeded();
		int needed = uneeded - progress;
		int nextlevel = level + 1;
		int maxlevel = Config.getJobConfig(job).getMaxLevel();
		
		if(exp >= needed) {
			if(nextlevel <= maxlevel) {
				level++;
				progress = exp - needed;
			} else progress = uneeded;
		} else progress += exp;

		if(level == nextlevel) return level;
		else return -1;
	}
	
	public int getNeeded() throws NotLoadedConfigException {
		return ExpUtils.getNeededExp(job, level + 1);
	}
	
}
