package ru.soknight.jobs.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.units.Workspace;
import ru.soknight.jobs.utils.Logger;
import ru.soknight.jobs.utils.Utils;

public class Config {

	public static FileConfiguration config;
	public static String prefix;
	public static boolean use_prefix;
	
	public static Map<JobType, Workspace> workspaces = new HashMap<>();
	
	public static void refresh() {
		Jobs instance = Jobs.getInstance();
		File datafolder = instance.getDataFolder();
		if(!datafolder.isDirectory()) datafolder.mkdirs();
		File file = new File(instance.getDataFolder(), "config.yml");
		if(!file.exists()) {
			try {
				Files.copy(instance.getResource("config.yml"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Logger.info("Generated new config file.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
		use_prefix = config.getBoolean("messages.use-prefix");
		if(use_prefix) prefix = config.getString("messages.prefix").replace("&", "\u00A7");
		reloadWorkspaces();
	}
	
	public static void reloadWorkspaces() {
		ConfigurationSection regions = config.getConfigurationSection("workspaces.regions");
		ConfigurationSection points = config.getConfigurationSection("workspaces.points");
		boolean centry = config.getBoolean("workspaces.centry");
		
		String ws = config.getString("workspaces.world");
		World world = BukkitAdapter.adapt(Bukkit.getWorld(ws));
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager manager = container.get(world);
		
		Utils.jobs.stream().forEach(j -> {
			String s = j.name().toLowerCase();
			String rs = regions.getString(s);
			
			if(!manager.hasRegion(rs)) {
				Logger.warning("Skipped loading the workspace for job '" + s + "': not found region by ID '" + rs 
						+ "' in world '" + ws + "'."); 
			} else {
				ProtectedRegion region = manager.getRegion(rs);
			
				String is = points.getString(s + ".internal");
				String es = points.getString(s + ".external");
			
				Location internal = Utils.parseLocation(is, centry);
				Location external = Utils.parseLocation(es, centry);
			
				Workspace workspace = new Workspace(internal, external, region);
				workspaces.put(j, workspace);
			}
		});
		
		Logger.info("Loaded " + workspaces.size() + " workspaces from config.");
	}
	
	public static List<String> getStringList(String section) {
		List<String> output = new ArrayList<>();
		for(String s : config.getStringList(section))
			output.add(s.replace("&", "\u00A7"));
		return output;
	}
	
}
