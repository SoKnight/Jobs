package ru.soknight.jobs.handlers;

import java.util.HashMap;
import java.util.Map;

import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.objects.JobBlock;
import ru.soknight.jobs.objects.JobEntity;
import ru.soknight.jobs.threads.RegenerateThread;
import ru.soknight.jobs.threads.RespawnThread;
import ru.soknight.jobs.utils.Logger;

public class JobTasksHandler {

	private static Map<JobType, RegenerateThread> regenThreads = new HashMap<>();
	private static RespawnThread respawnThread;
	
	public static void addBlock(JobType job, JobBlock jobBlock) {
		regenThreads.get(job).addBlock(job, jobBlock);
	}
	
	public static void addEntity(JobType job, JobEntity jobEntity) {
		respawnThread.addEntity(jobEntity);
	}
	
	public static void start() {
		RegenerateThread miner = new RegenerateThread();
		RegenerateThread woodcutter = new RegenerateThread();
		RegenerateThread farmer = new RegenerateThread();
		respawnThread = new RespawnThread();
		
		regenThreads.put(JobType.MINER, miner);
		regenThreads.put(JobType.WOODCUTTER, woodcutter);
		regenThreads.put(JobType.FARMER, farmer);
		Logger.info("Tasks started.");
	}
	
	public static void stop() {
		for(RegenerateThread thread : regenThreads.values())
			thread.close();
		respawnThread.close();
	}
	
}
