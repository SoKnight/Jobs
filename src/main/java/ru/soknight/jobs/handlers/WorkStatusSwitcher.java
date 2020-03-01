package ru.soknight.jobs.handlers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.objects.Workspace;

public class WorkStatusSwitcher {

	private static Map<String, JobType> workers = new HashMap<>();
	
	public static void start(Player p, JobType job) throws NotLoadedConfigException {
		Workspace workspace = Config.getJobConfig(job).getWorkspace();
		if(workspace == null) return;
		workers.put(p.getName(), job);
		
		// Refreshing workspace members
		ProtectedRegion region = workspace.getRegion();
		DefaultDomain members = region.getMembers();
		members.addPlayer(p.getName());
		region.setMembers(members);
		workspace.setRegion(region);
		
		// Teleporting
		Location location = workspace.getInternal();
		Location current = p.getLocation();
		
		location.setPitch(current.getPitch());
		location.setYaw(current.getYaw());
		
		p.teleport(location);
	}
	
	public static void done(OfflinePlayer op) throws NotLoadedConfigException {
		String name = op.getName();
		if(!workers.containsKey(name)) return;
		
		JobType job = workers.get(name);
		
		Workspace workspace = Config.getJobConfig(job).getWorkspace();
		if(workspace == null) return;
		
		// Refreshing workspace members
		ProtectedRegion region = workspace.getRegion();
		DefaultDomain members = region.getMembers();
		members.removePlayer(name);
		region.setMembers(members);
		workspace.setRegion(region);
		
		// Teleporting
		if(op.isOnline()) {
			Player p = Bukkit.getPlayer(op.getUniqueId());
			Location location = workspace.getExternal();
			Location current = p.getLocation();
		
			location.setPitch(current.getPitch());
			location.setYaw(current.getYaw());
		
			p.teleport(location);
		}
		workers.remove(name);
	}
	
}
