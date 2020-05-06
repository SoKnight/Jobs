package ru.soknight.jobs.database.profile;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.soknight.jobs.configuration.JobTypeEnum;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "workers")
public class PlayerProfile {

	@DatabaseField(id = true)
	private String player;
	
	@ForeignCollectionField(columnName = "profiles")
	private ForeignCollection<EmployeeProfile> jobsProfiles;
	
	@DatabaseField(columnName = "currentjob")
	private JobTypeEnum currentJob;

	public PlayerProfile(String player) {
		this.player = player;
	}
	
	public int getJobsCount() {
		return this.jobsProfiles.size();
	}
	
}
