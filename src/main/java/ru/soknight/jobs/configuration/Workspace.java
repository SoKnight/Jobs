package ru.soknight.jobs.configuration;

import org.bukkit.Location;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Workspace {

	private final Location internal, external;
	private ProtectedRegion region;
	
}
