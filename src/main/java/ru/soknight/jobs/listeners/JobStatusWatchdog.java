package ru.soknight.jobs.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sk89q.worldguard.domains.DefaultDomain;

import lombok.RequiredArgsConstructor;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.WorkerProfile;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.WorkStatusSwitcher;
import ru.soknight.jobs.objects.JobConfig;
import ru.soknight.jobs.objects.Workspace;

@RequiredArgsConstructor
public class JobStatusWatchdog implements Listener {

	private final DatabaseManager dbm;
	
	private void done(Player p) throws NotLoadedConfigException {
		String name = p.getName();
		
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null) return;
		
		JobType job = worker.getCurrentJob();
		String jname = Config.getJobConfig(job).getName();
		
		worker.setCurrentJob(null);
		dbm.updateProfile(worker);
		
		WorkStatusSwitcher.done(p);
		p.sendMessage(Messages.formatMessage("work-doned", "%job%", jname));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null) return;
		
		if(worker.getCurrentJob() == null) return;
		else
			try {
				done(p);
			} catch (NotLoadedConfigException e1) {
				return;
			}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		String name = p.getName();
		
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null) return;
		
		if(worker.getCurrentJob() == null) return;
		else
			try {
				done(p);
			} catch (NotLoadedConfigException e1) {
				return;
			}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		
		boolean fixWorkspaceLeaving = Config.getConfig().getBoolean("fix-workspace-leaving", true);
		if(!fixWorkspaceLeaving || e.getCause().equals(TeleportCause.UNKNOWN)) return;
		
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null) return;
		
		JobType job = worker.getCurrentJob();
		if(job == null) return;
		
		JobConfig config;
		try {
			config = Config.getJobConfig(job);
		} catch (NotLoadedConfigException e1) {
			return;
		}
		
		Workspace workspace = config.getWorkspace();
		if(workspace == null) return;
		
		// Refreshing workspace members
		DefaultDomain members = workspace.getRegion().getMembers();
		members.removePlayer(p.getName());
		workspace.getRegion().setMembers(members);
		
		worker.setCurrentJob(null);
		dbm.updateProfile(worker);
		
		String jname = config.getName();
		p.sendMessage(Messages.formatMessage("work-doned", "%job%", jname));
	}
	
}
