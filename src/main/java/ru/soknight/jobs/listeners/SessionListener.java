package ru.soknight.jobs.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import lombok.RequiredArgsConstructor;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.WorkspaceBlock;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.SessionManager;
import ru.soknight.jobs.objects.JobConfig;

@RequiredArgsConstructor
public class SessionListener implements Listener {
	
	private final SessionManager sessionManager;
	private final DatabaseManager dbm;
	
	@EventHandler
	public void onBlockClick(PlayerInteractEvent e) {
		Action action = e.getAction();
		if(!action.equals(Action.LEFT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;
		
		if(!e.getHand().equals(EquipmentSlot.HAND)) return;
		
		Player p = e.getPlayer();
		String name = p.getName();
		
		if(!sessionManager.hasSession(name)) return;
		
		JobType job = sessionManager.getSession(name).getJob();
		JobConfig config;
		try {
			config = Config.getJobConfig(job);
		} catch (NotLoadedConfigException e1) {
			return;
		}
		
		String jname = config.getName();
		
		Location location = e.getClickedBlock().getLocation();
		String world = location.getWorld().getName();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		
		if(action.equals(Action.LEFT_CLICK_BLOCK)) {
			if(!dbm.hasBlock(world, x, y, z))
				sendMessage(p, Messages.formatMessage("edit-remove-failed", "%job%", jname));
			else {
				sendMessage(p, Messages.formatMessage("edit-removed", "%job%", jname, "%x%", x, "%y%", y, "%z%", z));
			}
		} else {
			JobType jobblock = dbm.getJobForBlock(world, x, y, z);
			
			if(jobblock != null) {
				try {
					jname = Config.getJobConfig(jobblock).getName();
				} catch (NotLoadedConfigException e1) {
					return;
				}
				sendMessage(p, Messages.formatMessage("edit-add-failed", "%job%", jname));
			} else {
				dbm.saveBlock(new WorkspaceBlock(job, world, x, y, z));
				sendMessage(p, Messages.formatMessage("edit-added", "%job%", jname, "%x%", x, "%y%", y, "%z%", z));
			}
		}
		
		e.setCancelled(true);
	}
	
	private void sendMessage(Player p, String message) {
		if(Config.getConfig().getBoolean("messages.selection-actionbar"))
			p.sendActionBar(message);
		else p.sendMessage(message);
	}
	
}
