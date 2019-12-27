package ru.soknight.jobs.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.units.Workspace;

public class WorkStatusSwitcher {

	private static Map<String, JobType> workers = new HashMap<>();
	
	public static void start(Player p, JobType job) {
		Workspace workspace = Config.workspaces.get(job);
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
	
	public static void done(OfflinePlayer op, JobType job) {
		Workspace workspace = Config.workspaces.get(job);
		if(workspace == null) return;
		
		// Refreshing workspace members
		ProtectedRegion region = workspace.getRegion();
		DefaultDomain members = region.getMembers();
		members.removePlayer(op.getName());
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
		workers.remove(op.getName());
	}
	
	public static void doneAll() {
		if(workers.isEmpty()) return;
		for(String s : workers.keySet()) {
			OfflinePlayer op = Bukkit.getOfflinePlayer(s);
			JobType job = workers.get(s);
			done(op, job);
			if(op.isOnline()) 
				op.getPlayer().sendMessage(Utils.pformat("work-doned", "job@@" + job.getName()));
		}
//		workers.keySet().forEach(s -> {
//			if(s == null) return;
//			OfflinePlayer op = Bukkit.getOfflinePlayer(s);
//			JobType job = workers.get(s);
//			done(op, job);
//			if(op.isOnline()) 
//				op.getPlayer().sendMessage(Utils.pformat("work-doned", "job@@" + job.getName()));
//		});
	}
	
}
