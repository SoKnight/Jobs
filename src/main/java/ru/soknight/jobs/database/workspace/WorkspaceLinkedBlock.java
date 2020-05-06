package ru.soknight.jobs.database.workspace;

import org.bukkit.Location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.soknight.jobs.configuration.JobTypeEnum;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "blocks")
public class WorkspaceLinkedBlock {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private JobTypeEnum job;
	@DatabaseField
	private String world;
	@DatabaseField
	private int x;
	@DatabaseField
	private int y;
	@DatabaseField
	private int z;
	
	public WorkspaceLinkedBlock(JobTypeEnum job, String world, int x, int y, int z) {
		this.job = job;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static WorkspaceLinkedBlock fromLocation(JobTypeEnum job, Location location) {
		WorkspaceLinkedBlock block = new WorkspaceLinkedBlock();
		
		block.setJob(job);
		block.setWorld(location.getWorld().getName());
		block.setX(location.getBlockX());
		block.setY(location.getBlockY());
		block.setZ(location.getBlockZ());
		
		return block;
	}
	
}
