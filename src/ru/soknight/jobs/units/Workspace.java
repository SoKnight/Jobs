package ru.soknight.jobs.units;

import org.bukkit.Location;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Workspace {

	private Location internal, external;
	private ProtectedRegion region;
	
	public Workspace(Location internal, Location external, ProtectedRegion region) {
		this.internal = internal;
		this.external = external;
		this.region = region;
	}

	public Location getInternal() {
		return internal;
	}

	public Location getExternal() {
		return external;
	}

	public ProtectedRegion getRegion() {
		return region;
	}

	public void setRegion(ProtectedRegion region) {
		this.region = region;
	}
	
}
