package ru.soknight.jobs.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.SessionManager;
import ru.soknight.jobs.database.workspace.WorkspaceBlocksManager;
import ru.soknight.jobs.database.workspace.WorkspaceLinkedBlock;
import ru.soknight.lib.configuration.Messages;

public class SessionListener implements Listener {
	
	private final Jobs plugin;
	private final BukkitScheduler scheduler;
	
	private final Config config;
	private final Messages messages;
	
	private final SessionManager sessionManager;
	private final WorkspaceBlocksManager tilesManager;
	
	public SessionListener(Jobs plugin, Config config, Messages messages,
			SessionManager sessionManager, WorkspaceBlocksManager tilesManager) {
		
		this.plugin = plugin;
		this.scheduler = Bukkit.getScheduler();
		
		this.config = config;
		this.messages = messages;
		
		this.sessionManager = sessionManager;
		this.tilesManager = tilesManager;
	}
	
	public void register(Plugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onBlockClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(!action.equals(Action.LEFT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;
		
		if(!event.getHand().equals(EquipmentSlot.HAND)) return;
		
		Player p = event.getPlayer();
		String name = p.getName();
		
		if(!sessionManager.hasSession(name)) return;
		
		JobTypeEnum job = sessionManager.getSessionJob(name);
		
		JobConfiguration config = this.config.getJobConfig(job);
		if(config == null) return;
		
		String jname = config.getName();
		
		Location location = event.getClickedBlock().getLocation();
		
		String world = location.getWorld().getName();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		
		event.setCancelled(true);
		
		scheduler.runTaskAsynchronously(plugin, () -> {
			if(action.equals(Action.LEFT_CLICK_BLOCK)) {
				if(!tilesManager.isLinkedBlock(world, x, y, z))
					messages.sendFormatted(p, "selection.remove.failed",
							"%job%", jname);
				else
					messages.sendFormatted(p, "selection.remove.success",
							"%job%", jname,
							"%x%", x,
							"%y%", y,
							"%z%", z);
			} else {
				JobTypeEnum jobblock = tilesManager.getJobForLinkedBlock(world, x, y, z);
				
				if(jobblock != null) {
					String jobName = jname;
					try {
						jobName = this.config.getJobConfig(jobblock).getName();
					} catch (Exception ignored) {
						return;
					}
					messages.sendFormatted(p, "selection.add.failed",
							"%job%", jobName);
				} else {
					tilesManager.saveLinkedBlock(new WorkspaceLinkedBlock(job, world, x, y, z));
					messages.sendFormatted(p, "selection.add.success",
							"%job%", jname,
							"%x%", x,
							"%y%", y,
							"%z%", z);
				}
			}
		});
	}
	
}
