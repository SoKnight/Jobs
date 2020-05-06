package ru.soknight.jobs.database.profile;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "profiles")
public class EmployeeProfile {

	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField
	private String worker;
	
	@DatabaseField
	private JobTypeEnum jobType;
	
	@DatabaseField
	private int level;
	
	@DatabaseField
	private int progress;
	
	@DatabaseField(foreign = true)
	private PlayerProfile playerProfile;
	
	public EmployeeProfile(String owner, JobTypeEnum jobType) {
		this.worker = owner;
		this.jobType = jobType;
		this.level = 1;
		this.progress = 0;
	}
	
	public void increaseLevel() {
		level++;
	}
	
	public int addProgress(int exp, JobConfiguration config) {
		int uneeded = getNeeded(config);
		int needed = uneeded - progress;
		int nextlevel = level + 1;
		
		if(exp >= needed) {
			if(nextlevel <= config.getMaxLevel()) {
				level++;
				progress = exp - needed;
			} else progress = uneeded;
		} else progress += exp;

		if(level == nextlevel) return level;
		else return -1;
	}
	
	public int getNeeded(JobConfiguration config) {
		float multiplier = config.getExpMultiplier();
		float result = config.getFirstLevelExp();
		
		if(level > 1)
			for(int i = 1; i < level; i++) result *= multiplier;
		
		return (int) result;
	}
	
}
