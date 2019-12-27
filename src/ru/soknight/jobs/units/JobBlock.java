package ru.soknight.jobs.units;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class JobBlock {

	private Material material;
	private BlockData blockData;
	
	private Location location;
	private long time;
	
	public JobBlock(Material material, BlockData blockData, Location location) {
		this.material = material;
		this.blockData = blockData;
		this.location = location;
		this.time = System.currentTimeMillis() / 1000;
	}
	
	public void placeBedrock() {
		World world = location.getWorld();
		Block block = world.getBlockAt(location);
		
		block.setType(Material.BEDROCK);
	}
	
	public boolean regenerate(long current, int max) {
		if((current - time) < max) return false;
		
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
