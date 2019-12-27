package ru.soknight.jobs.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.sk89q.worldguard.domains.DefaultDomain;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.units.Workspace;
import ru.soknight.jobs.utils.Utils;
import ru.soknight.jobs.utils.WorkStatusSwitcher;

public class JobStatusWatchdog implements Listener {

	private void done(Player p) {
		String name = p.getName();
		
		JobType job = DatabaseManager.getCurrentJob(name);
		WorkStatusSwitcher.done(p, job);
		DatabaseManager.resetCurrentJob(name);
		
		p.sendMessage(Utils.pformat("work-doned", "job@@" + job.getName()));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		
		if(!DatabaseManager.isWorkNow(name)) return;
		else done(p);
		return;
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		String name = p.getName();
		
		if(!DatabaseManager.isWorkNow(name)) return;
		else done(p);
		return;
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		
		if(!DatabaseManager.isWorkNow(name)) return;
		
		JobType job = DatabaseManager.getCurrentJob(name);
		
		Workspace workspace = Config.workspaces.get(job);
		if(workspace == null) return;
		
		// Refreshing workspace members
		DefaultDomain members = workspace.getRegion().getMembers();
		members.removePlayer(p.getName());
		workspace.getRegion().setMembers(members);
		
		DatabaseManager.resetCurrentJob(name);
		p.sendMessage(Utils.pformat("work-doned", "job@@" + job.getName()));
		return;
	}
	
	
	
}
