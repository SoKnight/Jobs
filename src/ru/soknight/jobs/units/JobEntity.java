package ru.soknight.jobs.units;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public class JobEntity {

	private EntityType entity;
	
	private Location location;
	private long time;
	
	public JobEntity(EntityType entity, Location location) {
		this.entity = entity;
		this.location = location;
		this.time = System.currentTimeMillis() / 1000;
	}
	
	public boolean respawn(long current, int max) {
		if((current - time) < max) return false;
		
		World world = location.getWorld();
		world.spawnEntity(location, entity);
		return true;
	}
	
	public boolean respawn() {
		World world = location.getWorld();
		world.spawnEntity(location, entity);
		return true;
	}
	
}
