package ru.soknight.jobs.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.profile.EmployeeProfile;
import ru.soknight.jobs.database.profile.PlayerProfile;
import ru.soknight.jobs.database.profile.ProfilesManager;
import ru.soknight.jobs.database.workspace.WorkspaceBlocksManager;
import ru.soknight.jobs.thread.BlocksRegenerator;
import ru.soknight.jobs.thread.ReplacementBlock;
import ru.soknight.jobs.tool.EconomyManager;
import ru.soknight.jobs.tool.LevelUpHelper;
import ru.soknight.lib.configuration.Messages;

public class WorkingListener implements Listener {
	
	private final Config config;
	private final BlocksRegenerator regenerator;
	
	private final ProfilesManager profilesManager;
	private final WorkspaceBlocksManager tilesManager;
	
	private final EconomyManager economyManager;
	private final LevelUpHelper levelUpHelper;
	
	public WorkingListener(Jobs plugin, Config config, Messages messages,
			ProfilesManager profilesManager, WorkspaceBlocksManager tilesManager) {
		this.config = config;
		this.regenerator = new BlocksRegenerator(plugin, config);
		
		this.economyManager = new EconomyManager(plugin, messages);
		this.levelUpHelper = new LevelUpHelper(config, messages);
		
		this.profilesManager = profilesManager;
		this.tilesManager = tilesManager;
	}
	
	public void register(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void shutdownRegenerator() {
		regenerator.shutdown();
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player breaker = event.getPlayer();
		String name = breaker.getName();
		
		PlayerProfile worker = profilesManager.getProfile(name);
		if(worker == null) return;
		
		JobTypeEnum job = worker.getCurrentJob();
		if(job == null) return;
		
		EmployeeProfile profile = profilesManager.getEmployeeProfile(name, job);
		if(profile == null) return;
		
		Block block = event.getBlock();
		Material material = block.getType();
		Location loc = block.getLocation();
		
		JobConfiguration config = this.config.getJobConfig(job);
		if(config == null || !config.isInitialized()) return;
		
		event.setCancelled(true);
		
		String world = loc.getWorld().getName();
		JobTypeEnum jobOfBlock = tilesManager.getJobForLinkedBlock(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		// Check if this block isn't owned by this job
		if(jobOfBlock == null || !jobOfBlock.equals(job))
			// Sub-check if this block's type isn't allowed by this job's configuration
			if(!config.isUseBlocktypes())
				return;
			else if(!config.getBlocktypes().contains(material))
				return;
		
		event.setExpToDrop(0);
		event.setDropItems(false);
		
		int nextlevel = profile.addProgress(1, config);
		if(nextlevel != -1)
			levelUpHelper.sendLevelUpMessage(breaker, nextlevel);
			
		profilesManager.saveEmployeeProfile(profile);
			
		ReplacementBlock jobBlock = new ReplacementBlock(config, block);
		regenerator.addBlock(job, jobBlock);
		
		float salary = config.getFinalSalary(breaker, profile.getLevel(), material);
		economyManager.sendSalary(breaker, config, salary);
	}
}
