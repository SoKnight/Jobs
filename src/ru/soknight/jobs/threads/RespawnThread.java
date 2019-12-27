package ru.soknight.jobs.threads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.units.JobEntity;

public class RespawnThread extends BukkitRunnable {

	private int max;
	private List<JobEntity> entities = new ArrayList<>();
	
	public RespawnThread(int max) {
		this.max = max;
		this.runTaskTimer(Jobs.getInstance(), 1, 1);
	}
	
	public void addEntity(JobEntity jobEntity) {
		entities.add(jobEntity);
	}
	
	public void close() {
		entities.removeIf(entity -> entity.respawn());
	}

	@Override
	public void run() {
		long current = System.currentTimeMillis() / 1000;
		entities.removeIf(entity -> entity.respawn(current, max));
	}
	
}