package ru.soknight.jobs.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import lombok.Getter;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.configuration.Workspace;
import ru.soknight.jobs.database.profile.PlayerProfile;
import ru.soknight.jobs.database.profile.ProfilesManager;
import ru.soknight.lib.configuration.Messages;

public class EmployeesWatchdog implements Listener {

	private final Config config;
	private final Messages messages;
	
	private final ProfilesManager profilesManager;
	@Getter private final Map<String, JobTypeEnum> employees;
	
	public EmployeesWatchdog(Config config, Messages messages, ProfilesManager profilesManager) {
		this.config = config;
		this.messages = messages;
		
		this.profilesManager = profilesManager;
		this.employees = new HashMap<>();
	}
	
	public void register(Plugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void onWorkingStarted(Player player, JobTypeEnum job) {
		JobConfiguration config = this.config.getJobConfig(job);
		if(config == null) return;
		
		Workspace workspace = config.getWorkspace();
		if(workspace == null) return;
		
		// Refreshing workspace members
		ProtectedRegion region = workspace.getRegion();
		DefaultDomain members = region.getMembers();
		members.addPlayer(player.getName());
		region.setMembers(members);
		workspace.setRegion(region);
		
		// Teleporting
		Location location = workspace.getInternal();
		Location current = player.getLocation();
		
		location.setPitch(current.getPitch());
		location.setYaw(current.getYaw());
		
		player.teleport(location);
		
		employees.put(player.getName(), job);
	}
	
	public void onWorkingFinished(OfflinePlayer player) {
		String name = player.getName();
		
		PlayerProfile worker = profilesManager.getProfile(name);
		if(worker == null) return;
		
		JobTypeEnum job = worker.getCurrentJob();
		
		JobConfiguration config = this.config.getJobConfig(job);
		if(config == null) return;
		
		String jname = config.getName();
		
		worker.setCurrentJob(null);
		profilesManager.updateProfile(worker);
		
		if(employees.containsKey(name)) {
			Workspace workspace = config.getWorkspace();
			if(workspace != null) {
				// Refreshing workspace members
				ProtectedRegion region = workspace.getRegion();
				DefaultDomain members = region.getMembers();
				members.removePlayer(name);
				region.setMembers(members);
				workspace.setRegion(region);
			}
			
			employees.remove(name);
			
			// Teleporting
			if(player.isOnline()) {
				Player p = player.getPlayer();
				
				Location location = workspace.getExternal();
				Location current = p.getLocation();
			
				location.setPitch(current.getPitch());
				location.setYaw(current.getYaw());
			
				p.teleport(location);
				
				messages.sendFormatted(p, "working.finish.success", "%job%", jname);
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		String name = p.getName();
		
		PlayerProfile worker = profilesManager.getProfile(name);
		if(worker == null) return;
		
		if(worker.getCurrentJob() != null)
			onWorkingFinished(p);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		String name = p.getName();
		
		PlayerProfile worker = profilesManager.getProfile(name);
		if(worker == null) return;
		
		if(worker.getCurrentJob() != null)
			onWorkingFinished(p);
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
//		boolean fixWorkspaceLeaving = config.getBoolean("fix-workspace-leaving", true);
//		if(!fixWorkspaceLeaving || e.getCause() == TeleportCause.UNKNOWN)
//			return;
		
		Player p = e.getPlayer();
		String name = p.getName();
		
		if(employees.containsKey(name))
			e.setCancelled(true);
		
//		PlayerProfile worker = profilesManager.getProfile(name);
//		if(worker == null) return;
//
//		if(worker.getCurrentJob() != null)
//			onWorkingFinished(p);
	}
	
}
