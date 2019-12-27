package ru.soknight.jobs.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;

public class Utils {

	public static final List<JobType> jobs = Arrays.asList(JobType.MINER, JobType.WOODCUTTER, JobType.HUNTER, 
			JobType.FISHERMAN, JobType.FARMER);
	
	public static int getNeededExp(int nextlevel) {
		double multiplier = Config.config.getDouble("levels.exp-multiplier");
		double first = Config.config.getDouble("levels.first-level-exp");
		double result = first * multiplier;
		if(nextlevel > 2) for(int i = 2; i < nextlevel; i++) result *= multiplier;
		return (int) result;
	}
	
	public static String format(String section, String... replacements) {
		String raw = Messages.getRawMessage(section);
		for(String r : replacements) {
			String[] parts = r.split("@@");
			raw = raw.replace("%" + parts[0] + "%", parts[1]);
		}
		return raw;
	}
	
	public static String pformat(String section, String... replacements) {
		String raw = Messages.getMessage(section);
		for(String r : replacements) {
			String[] parts = r.split("@@");
			raw = raw.replace("%" + parts[0] + "%", parts[1]);
		}
		return raw;
	}
	
	public static String formatLocation(Location location) {
		int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
		String output = x + ", " + y + ", " + z;
		return output;
	}
	
	public static Location parseLocation(String src, boolean centry) {
		String[] parts = src.split(";");
		World world = Bukkit.getWorld(parts[0]);
		double x = Double.parseDouble(parts[1]);
		double y = Double.parseDouble(parts[2]);
		double z = Double.parseDouble(parts[3]);
		if(centry) { x += 0.5D; z += 0.5D; }
		Location output = new Location(world, x, y, z);
		return output;
	}
	
	public static List<String> fixListColors(List<String> raw) {
		List<String> output = new ArrayList<>();
		for(String s : raw)
			output.add(s.replace("&", "\u00A7"));
		return output;
	}
	
}
