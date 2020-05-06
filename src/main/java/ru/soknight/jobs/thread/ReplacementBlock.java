
package ru.soknight.jobs.thread;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import ru.soknight.jobs.configuration.JobConfiguration;

public class ReplacementBlock {

	private final BlockData blockData;
	private final Material replacement;
	private final Location location;
	
	private final long changeTime;
	private final long maxTime;
	
	public ReplacementBlock(JobConfiguration config, Block block) {
		this.blockData = block.getBlockData().clone();
		this.replacement = config.getReplacement();
		this.location = block.getLocation();
		this.changeTime = System.currentTimeMillis() / 1000;
		
		Material material = blockData.getMaterial();
		this.maxTime = config.getTaskPeriod(material);
	}
	
	public void replace() {
		World world = location.getWorld();
		Block block = world.getBlockAt(location);
		
		block.setType(replacement);
	}
	
	public boolean regenerate(long current) {
		if(current - changeTime < maxTime) return false;
		
		World world = location.getWorld();
		Block block = world.getBlockAt(location);
		
		block.setBlockData(blockData);
		return true;
	}
	
	public boolean regenerate() {
		World world = location.getWorld();
		Block block = world.getBlockAt(location);
		
		block.setBlockData(blockData);
		return true;
	}
	
}
