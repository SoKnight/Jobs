package ru.soknight.jobs.threads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.units.JobBlock;

public class RegenerateThread extends BukkitRunnable {

	private int max;
	private List<JobBlock> blocks = new ArrayList<>();
	
	public RegenerateThread(int max) {
		this.max = max;
		this.runTaskTimer(Jobs.getInstance(), 1, 1);
	}
	
	public void addBlock(JobType job, JobBlock jobBlock) {
		if(job.equals(JobType.MINER)) jobBlock.placeBedrock();
		blocks.add(jobBlock);
	}
	
	public void close() {
		blocks.removeIf(block -> block.regenerate());
	}

	@Override
	public void run() {
		long current = System.currentTimeMillis() / 1000;
		blocks.removeIf(block -> block.regenerate(current, max));
	}
	
}
