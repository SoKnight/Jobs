package ru.soknight.jobs.database;

import org.bukkit.Material;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.soknight.jobs.enums.JobType;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "blocktypes")
public class WorkspaceBlocktype {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private JobType job;
	@DatabaseField
	private Material material;
	
	public WorkspaceBlocktype(JobType job, Material material) {
		this.job = job;
		this.material = material;
	}
	
}
