package ru.soknight.jobs.thread;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobTypeEnum;

public class BlocksRegenerator implements Runnable {

	private final List<ReplacementBlock> blocks;
	
	public BlocksRegenerator(Jobs plugin, Config config) {
		this.blocks = new CopyOnWriteArrayList<>();
		Bukkit.getScheduler().runTaskTimer(plugin, this, 0, config.getInt("regenerator-period", 20));
		plugin.getLogger().info("Regenerator task has been started.");
	}
	
	public void addBlock(JobTypeEnum job, ReplacementBlock jobBlock) {
		jobBlock.replace();
		blocks.add(jobBlock);
	}
	
	@Override
	public void run() {
		long current = System.currentTimeMillis() / 1000;
		blocks.removeIf(block -> block.regenerate(current));
	}
	
	public void shutdown() {
		blocks.removeIf(ReplacementBlock::regenerate);
	}
	
}
