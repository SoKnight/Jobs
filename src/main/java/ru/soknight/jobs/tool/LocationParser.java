package ru.soknight.jobs.tool;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationParser {
	
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
