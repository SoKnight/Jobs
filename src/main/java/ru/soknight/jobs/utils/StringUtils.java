package ru.soknight.jobs.utils;

import org.bukkit.Location;
import org.bukkit.World;

public class StringUtils {

	public static String format(String source, Object... replacements) {
		for(int i = 0; i < replacements.length; i++) {
			String replacement = replacements[i].toString();
			if(replacement.startsWith("%") && replacement.endsWith("%")) continue;
			
			String node = replacements[i - 1].toString();
			source = source.replace(node, replacement);
		}
		return source;
	}
	
	public static String capitalizeFirst(String source) {
		return Character.toUpperCase(source.charAt(0)) + source.substring(1).toLowerCase();
	}
	
	public static Location getLocationFromString(String source, World world) {
		String[] parts = source.split(", ");
		
		double x = Double.parseDouble(parts[0]);
		double y = Double.parseDouble(parts[1]);
		double z = Double.parseDouble(parts[2]);
		
		Location output = new Location(world, x, y, z);
		return output;
	}
	
	public static String getStringFromLocation(Location location) {
		String output = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
		return output;
	}
	
}
