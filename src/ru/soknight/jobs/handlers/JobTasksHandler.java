package ru.soknight.jobs.handlers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.threads.RegenerateThread;
import ru.soknight.jobs.threads.RespawnThread;
import ru.soknight.jobs.units.JobBlock;
import ru.soknight.jobs.units.JobEntity;
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
		ConfigurationSection config = Config.config.getConfigurationSection("tasks");
		int maxMiner = config.getInt("miner"), maxWoodcutter = config.getInt("woodcutter"),
		maxFarmer = config.getInt("farmer"), maxHunter = config.getInt("hunter");
		
		RegenerateThread miner = new RegenerateThread(maxMiner);
		RegenerateThread woodcutter = new RegenerateThread(maxWoodcutter);
		RegenerateThread farmer = new RegenerateThread(maxFarmer);
		respawnThread = new RespawnThread(maxHunter);
		
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
