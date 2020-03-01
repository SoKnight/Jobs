package ru.soknight.jobs.objects;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;

public class JobEntity {

	private EntityType entity;
	private Location location;
	
	private long changeTime;
	private long maxTime;
	
	public JobEntity(EntityType entity, Location location) throws NotLoadedConfigException {
		this.entity = entity;
		this.location = location;
		this.changeTime = System.currentTimeMillis() / 1000;
		
		JobConfig config = Config.getJobConfig(JobType.HUNTER);
		int defaultTime = config.getDefaultTaskPeriod();
		
		Map<EntityType, Integer> periods = config.getCustomEntitiesTaskPeriods();
		this.maxTime = periods.containsKey(entity) ? periods.get(entity) : defaultTime;
	}
	
	public boolean respawn(long current) {
		if((current - changeTime) < maxTime) return false;
		
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
