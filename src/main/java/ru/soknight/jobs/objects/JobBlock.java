package ru.soknight.jobs.objects;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;

public class JobBlock {

	private JobType job;
	private Material material;
	private BlockData blockData;
	private Location location;
	
	private long changeTime;
	private long maxTime;
	
	public JobBlock(JobType job, Material material, BlockData blockData, Location location) throws NotLoadedConfigException {
		this.job = job;
		this.material = material;
		this.blockData = blockData;
		this.location = location;
		this.changeTime = System.currentTimeMillis() / 1000;
		
		JobConfig config = Config.getJobConfig(job);
		
		int defaultTime = config.getDefaultTaskPeriod();
		
		Map<Material, Integer> periods = config.getCustomTaskPeriods();
		this.maxTime = periods.containsKey(material) ? periods.get(material) : defaultTime;
	}
	
	public void replace() {
		World world = location.getWorld();
		Block block = world.getBlockAt(location);
		
		Material replacement;
		try {
			replacement = Config.getJobConfig(job).getReplacement();
		} catch (NotLoadedConfigException e) {
			return;
		}
		block.setType(replacement);
	}
	
	public boolean regenerate(long current) {
		if((current - changeTime) < maxTime) return false;
		
		World world = location.getWorld();
		Block block = world.getBlockAt(location);
		
		block.setType(material);
		block.setBlockData(blockData);
		return true;
	}
	
	public boolean regenerate() {
		World world = location.getWorld();
		Block block = world.getBlockAt(location);
		
		block.setType(material);
		block.setBlockData(blockData);
		return true;
	}
	
}
