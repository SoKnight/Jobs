package ru.soknight.jobs.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import lombok.Data;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.JobConfigLoadException;
import ru.soknight.jobs.utils.StringUtils;

@Data
public class JobConfig {

	private final JobType type;
	private String name, typeLower, salaryWallet;
	private Workspace workspace;
	private int maxLevel, firstLevelExp, defaultTaskPeriod;
	private float expMultiplier, baseSalary, levelBoost;
	private boolean initialized, useChildTask;
	private Material replacement;
	private Map<Material, Integer> customTaskPeriods = new HashMap<>();
	private Map<EntityType, Integer> customEntitiesTaskPeriods = new HashMap<>();
	private Map<EntityType, Float> entitiesBoosts = new HashMap<>();
	private Map<Material, Float> materialsBoosts = new HashMap<>();
	private Map<String, Float> permissionsBoosts = new HashMap<>();
	
	public JobConfig(JobType type, ConfigurationSection config) throws JobConfigLoadException {
		this.initialized = false;
		this.type = type;
		this.typeLower = type.name().toLowerCase();
		loadFromSection(config);
	}
	
	private void loadFromSection(ConfigurationSection config) throws JobConfigLoadException {
		this.name = config.getString("name", StringUtils.capitalizeFirst(typeLower));
		
		/*
		 * World initilization
		 */
		String worldname = config.getString("world", "world");
		World world = Bukkit.getWorld(worldname);
		if(world == null)
			throw new JobConfigLoadException(typeLower, "Unknown world name (" + worldname + ")");
		
		/*
		 * Workspace initilization
		 */
		String region = config.getString("workspace", typeLower + "_ws");
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager manager = container.get(BukkitAdapter.adapt(world));
		
		if(!manager.hasRegion(region))
			throw new JobConfigLoadException(typeLower, "Unknown workspace region (" + region + ")");
		
		String internal = config.getString("internal", "0.5, 70, 0.5");
		String external = config.getString("external", "5.5, 70, 5.5");
		
		Location inloc, exloc;
		
		try {
			inloc = StringUtils.getLocationFromString(internal, world);
		} catch (Exception e) {
			throw new JobConfigLoadException(typeLower, "Failed get location from string (" + internal + ")");
		}
		
		try {
			exloc = StringUtils.getLocationFromString(external, world);
		} catch (Exception e) {
			throw new JobConfigLoadException(typeLower, "Failed get location from string (" + external + ")");
		}
		
		this.workspace = new Workspace(inloc, exloc, manager.getRegion(region));
		
		/*
		 * Leveling initialization
		 */
		this.maxLevel = config.getInt("max-level", 30);
		this.firstLevelExp = config.getInt("first-level-exp", 30);
		this.expMultiplier = (float) config.getDouble("exp-multiplayer", 1.7);
		
		/*
		 * Salary initialization
		 */
		this.baseSalary = config.getInt("base-salary", 10);
		this.levelBoost = (float) config.getDouble("level-boost", 0.5);
		this.salaryWallet = config.getString("salary-wallet", "dollars");
		
		/*
		 * Permissions boosts initialization
		 */
		List<String> permsBoostsList = config.getStringList("permissions-boosts");
		
		if(permsBoostsList != null && !permsBoostsList.isEmpty()) {
			for(String permBoost : permsBoostsList) {
				try {
					String[] parts = permBoost.split(";");
					String permission = parts[0];
					float boost = Float.parseFloat(parts[1]);
					this.permissionsBoosts.put(permission, boost);
				} catch (Exception e) {
					throw new JobConfigLoadException(typeLower, "Failed load boost for permission (" + permBoost + ")");
				}
			}
		}
		
		/*
		 * Materials boosts initialization
		 */
		ConfigurationSection materialsBoosts = config.getConfigurationSection("material-boosts");
		Set<String> materialsBoostsKeys = materialsBoosts != null ? materialsBoosts.getKeys(false) : null;
		
		if(materialsBoostsKeys != null && !materialsBoostsKeys.isEmpty()) {
			for(String key : materialsBoostsKeys) {
				try {
					Material material = Material.valueOf(key.toUpperCase());
					if(material == null)
						throw new JobConfigLoadException(typeLower, "Failed load boost for material (" + key + ")");
					
					float boost = (float) materialsBoosts.getDouble(key);
					this.materialsBoosts.put(material, boost);
				} catch (Exception e) {
					throw new JobConfigLoadException(typeLower, "Failed load boost for material (" + key + ")");
				}
			}
		}
		
		/*
		 * Entity boosts initialization
		 */
		ConfigurationSection entitiesBoosts = config.getConfigurationSection("entities-boosts");
		Set<String> entitiesBoostsKeys = entitiesBoosts != null ? entitiesBoosts.getKeys(false) : null;
		
		if(entitiesBoostsKeys != null && !entitiesBoostsKeys.isEmpty()) {
			for(String key : entitiesBoostsKeys) {
				try {
					EntityType entityType = EntityType.valueOf(key.toUpperCase());
					if(entityType == null)
						throw new JobConfigLoadException(typeLower, "Failed load boost for entity type (" + key + ")");
					
					float boost = (float) entitiesBoosts.getDouble(key);
					this.entitiesBoosts.put(entityType, boost);
				} catch (Exception e) {
					throw new JobConfigLoadException(typeLower, "Failed load boost for entity type (" + key + ")");
				}
			}
		}
		
		/*
		 * Tasks parameters initialization
		 */
		this.useChildTask = config.getBoolean("use-child-task", false);
		this.defaultTaskPeriod = config.getInt("default-task-period", 10);
		
		String replacementstr = config.getString("replacement", "air");
		this.replacement = Material.valueOf(replacementstr.toUpperCase());
		if(this.replacement == null)
			throw new JobConfigLoadException(typeLower, "Unknown replacement material (" + replacementstr + ")");
		
		/*
		 * Custom tasks periods initialization
		 */
		ConfigurationSection customTasks = config.getConfigurationSection("custom-task-periods");
		Set<String> customTaskPeriods = customTasks != null ? customTasks.getKeys(false) : null;
		
		if(customTaskPeriods != null && !customTaskPeriods.isEmpty()) {
			for(String key : customTaskPeriods) {
				try {
					Material material = Material.valueOf(key.toUpperCase());
					if(material == null)
						throw new JobConfigLoadException(typeLower, "Failed load custom task period for material (" + key + ")");
					
					int period = customTasks.getInt(key);
					this.customTaskPeriods.put(material, period);
				} catch (Exception e) {
					throw new JobConfigLoadException(typeLower, "Failed load custom task period for material (" + key + ")");
				}
			}
		}
		
		/*
		 * Custom entities tasks periods initialization
		 */
		ConfigurationSection customEntitiesTasks = config.getConfigurationSection("custom-entities-task-periods");
		Set<String> customEntitiesTaskPeriods = customEntitiesTasks != null ? customEntitiesTasks.getKeys(false) : null;
		
		if(customEntitiesTaskPeriods != null && !customEntitiesTaskPeriods.isEmpty()) {
			for(String key : customEntitiesTaskPeriods) {
				try {
					EntityType entityType = EntityType.valueOf(key.toUpperCase());
					if(entityType == null)
						throw new JobConfigLoadException(typeLower, "Failed load custom task period for entity type (" + key + ")");
					
					int period = customEntitiesTasks.getInt(key);
					this.customEntitiesTaskPeriods.put(entityType, period);
				} catch (Exception e) {
					throw new JobConfigLoadException(typeLower, "Failed load custom task period for entity type (" + key + ")");
				}
			}
		}
		
		// Initialization finished, change status
		this.initialized = true;
	}
	
	/*
	 * Salary
	 */
	
	public float getLevelSalary(int level) {
		return baseSalary + levelBoost * ((float) level - 1);
	}
	
	public float getFinalSalary(Player player, int level, float boost) {
		float salary = getLevelSalary(level);
		float pboost = getBoost(player);
		return salary * boost * pboost;
	}

	public float getFinalSalary(Player player, int level, Material material) {
		float salary = getLevelSalary(level);
		float boost = getBoost(material);
		float pboost = getBoost(player);
		return salary * boost * pboost;
	}

	public float getFinalSalary(Player player, int level, EntityType entity) {
		float salary = getLevelSalary(level);
		float boost = getBoost(entity);
		float pboost = getBoost(player);
		return salary * boost * pboost;
	}

	/*
	 * Boosts
	 */
	
	public boolean hasBoost(Material material) {
		return materialsBoosts.containsKey(material);
	}
	
	public boolean hasBoost(EntityType entity) {
		return entitiesBoosts.containsKey(entity);
	}

	public boolean hasBoost(Player player) {
		for(String p : permissionsBoosts.keySet())
			if(player.hasPermission(p)) return true;
		return false;
	}

	public float getBoost(Material material) {
		return materialsBoosts.containsKey(material) ? materialsBoosts.get(material) : 1.0f;
	}
	
	public float getBoost(EntityType entity) {
		return entitiesBoosts.containsKey(entity) ? entitiesBoosts.get(entity) : 1.0f;
	}

	public float getBoost(Player player) {
		float boost = 1.0f;
		for(String p : permissionsBoosts.keySet())
			if(player.hasPermission(p)) {
				float pboost = permissionsBoosts.get(p);
				if(pboost > boost) boost = pboost;
			}
		return boost;
	}
	
}
