package ru.soknight.jobs.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.utils.Logger;

public class JobInfo{

	private String joblower;
	private JobType job;
	private double base, perlevel;
	private int maxlevel;
	private List<String> workers = new ArrayList<>();
	private Map<Material, Double> materials = new HashMap<>();
	private Map<String, Double> permissions = new HashMap<>();
	
	public JobInfo(JobType job) {
		this.job = job;
		this.joblower = job.name().toLowerCase();
		this.base = Config.config.getDouble("salaries." + joblower + ".base");
		this.perlevel = Config.config.getDouble("salaries." + joblower + ".per-level");
		this.maxlevel = Config.config.getInt("levels.max-levels." + joblower);
		loadBoosts();
	}

	public JobType getType() {
		return job;
	}
	
	private void loadBoosts() {
		// Materials
		ConfigurationSection ms = Config.config.getConfigurationSection("boosts.materials." + joblower);
		Set<String> mlist = ms.getKeys(false);
		if(!mlist.isEmpty())
			for(String m : mlist)
				try {
					materials.put(Material.valueOf(m.toUpperCase()), ms.getDouble(m));
				} catch (Exception e) {
					Logger.error("Couldn't set " + joblower + "'s boost for material '" + m + "': Invalid enum constant.");
				}
		// Permissions
		ConfigurationSection ps = Config.config.getConfigurationSection("boosts.permissions");
		Set<String> plist = ps.getKeys(true);
		if(!plist.isEmpty())
			for(String p : plist)
				if(ps.isSet(p)) permissions.put(p.replace("'", ""), ps.getDouble(p));
	}
	
	//
	// Salary
	//
	
	public double getBaseSalary() {
		return base;
	}
	
	public double getPerLevelSalary() {
		return perlevel;
	}

	public double getLevelSalary(double level) {
		return base + (perlevel * (level - 1));
	}
	
	public double getFinalSalary(Player player, double level, Material material) {
		double salary = getLevelSalary(level);
		double boost = getBoost(material) + 1.0d;
		double pboost = getBoost(player) + 1.0d;
		return salary * boost * pboost;
	}
	
	public double getFinalSalary(Player player, double level, double materialboost) {
		double salary = getLevelSalary(level);
		double pboost = getBoost(player) + 1.0d;
		return salary * materialboost * pboost;
	}
	
	//
	// Boosts
	//
	
	public boolean hasBoost(Material material) {
		return materials.containsKey(material);
	}
	
	public boolean hasBoost(Player player) {
		boolean has = false;
		for(String p : permissions.keySet())
			if(player.hasPermission(p)) has = true;
		return has;
	}
	
	public double getBoost(Material material) {
		double boost = 0.0;
		if(materials.containsKey(material)) 
			boost = materials.get(material);
		return boost;
	}
	
	public double getBoost(Player player) {
		double boost = 0;
		for(String p : permissions.keySet())
			if(!player.hasPermission(p)) continue;
			else {
				double pboost = permissions.get(p);
				if(pboost > boost) boost = pboost;
			}
		return boost;
	}
	
	public Map<Material, Double> getBoosts() {
		return materials;
	}
	
	//
	// Workers
	//
	
	public void addWorker(String name) {
		workers.add(name);
	}

	public List<String> getWorkers() {
		return workers;
	}
	
	public int getWorkersCount() {
		return workers.size();
	}
	
	public boolean isWorker(String name) {
		return workers.contains(name);
	}
	
	public void removeWorker(String name) {
		workers.remove(name);
	}
	
	//
	// Max level
	//

	public int getMaxLevel() {
		return maxlevel;
	}

	public void setMaxLevel(int maxlevel) {
		this.maxlevel = maxlevel;
	}
	
}
