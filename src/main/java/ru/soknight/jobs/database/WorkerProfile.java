package ru.soknight.jobs.database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.soknight.jobs.enums.JobType;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "workers")
public class WorkerProfile {

	@DatabaseField(id = true)
	private String name;
	@ForeignCollectionField(columnName = "profiles")
	private ForeignCollection<JobProfile> jobProfiles;
	@DatabaseField(columnName = "currentjob")
	private JobType currentJob;

	public WorkerProfile(String name) {
		this.name = name;
	}
	
	public int getJobCount() {
		return jobProfiles.size();
	}
	
}
