package ru.soknight.jobs.database;

import org.bukkit.Location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.soknight.jobs.enums.JobType;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "blocks")
public class WorkspaceBlock {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private JobType job;
	@DatabaseField
	private String world;
	@DatabaseField
	private int x;
	@DatabaseField
	private int y;
	@DatabaseField
	private int z;
	
	public WorkspaceBlock(JobType job, String world, int x, int y, int z) {
		this.job = job;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static WorkspaceBlock fromLocation(JobType job, Location location) {
		WorkspaceBlock block = new WorkspaceBlock();
		
		block.setJob(job);
		block.setWorld(location.getWorld().getName());
		block.setX(location.getBlockX());
		block.setY(location.getBlockY());
		block.setZ(location.getBlockZ());
		
		return block;
	}
	
}
