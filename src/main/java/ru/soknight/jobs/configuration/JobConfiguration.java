package ru.soknight.jobs.configuration;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import lombok.Getter;
import lombok.Setter;
import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.exception.JobConfigLoadException;
import ru.soknight.jobs.tool.LocationParser;
import ru.soknight.lib.configuration.AbstractConfiguration;

@Getter
@Setter
public class JobConfiguration extends AbstractConfiguration {

	private final JobTypeEnum jobType;
	private final Logger logger;

	private String name;
	private String salaryCurrency;
	
	private Workspace workspace;
	
	private int maxLevel;
	private int firstLevelExp;
	private int defaultTaskPeriod;
	
	private float expMultiplier;
	private float baseSalary;
	private float levelBoost;
	
	private boolean initialized;
	private boolean useTask;
	private boolean useBlocktypes;
	
	private Material replacement;
	private List<Material> blocktypes;
	
	private Map<Material, Integer> taskPeriods = new HashMap<>();
	private Map<Material, Float> materialsBoosts = new HashMap<>();
	private Map<String, Float> permissionsBoosts = new HashMap<>();
	
	public JobConfiguration(Jobs plugin, File datafolder, InputStream source, JobTypeEnum jobType)
			throws JobConfigLoadException {
		
		super(plugin, datafolder, source, jobType.toString().toLowerCase() + ".yml");
		
		this.initialized = false;
		
		super.refresh(false);
		
		this.logger = plugin.getLogger();
		this.jobType = jobType;
		
		initialize();
	}
	
	private void initialize() throws JobConfigLoadException {
		if(getFileConfig() == null) return;
		
		String typeLower = WordUtils.capitalize(this.jobType.toString().toLowerCase());
		this.name = getColoredString("name", typeLower);
		
		/*
		 * World initilization
		 */
		String worldname = getString("world", "world");
		World world = Bukkit.getWorld(worldname);
		if(world == null)
			throw new JobConfigLoadException(jobType, "Unknown world name '" + worldname + "'");
		
		/*
		 * Workspace initilization
		 */
		String region = getString("workspace", typeLower + "_ws");
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager manager = container.get(BukkitAdapter.adapt(world));
		
		if(!manager.hasRegion(region))
			throw new JobConfigLoadException(jobType, "Unknown workspace region '" + region + "'");
		
		String internal = getFileConfig().getString("internal", "0.5, 70, 0.5");
		String external = getFileConfig().getString("external", "5.5, 70, 5.5");
		
		Location inloc, exloc;
		
		try {
			inloc = LocationParser.getLocationFromString(internal, world);
		} catch (Exception e) {
			throw new JobConfigLoadException(jobType, "Failed to parse location from '" + internal + "'");
		}
		
		try {
			exloc = LocationParser.getLocationFromString(external, world);
		} catch (Exception e) {
			throw new JobConfigLoadException(jobType, "Failed to parse location from '" + external + "'");
		}
		
		this.workspace = new Workspace(inloc, exloc, manager.getRegion(region));
		
		/*
		 * Leveling initialization
		 */
		this.maxLevel = getInt("max-level", 30);
		this.firstLevelExp = getInt("first-level-exp", 30);
		this.expMultiplier = (float) getDouble("exp-multiplayer", 1.7);
		
		/*
		 * Salary initialization
		 */
		this.baseSalary = getInt("base-salary", 10);
		this.levelBoost = (float) getDouble("level-boost", 0.5);
		this.salaryCurrency = getString("salary-wallet", "dollars");
		
		/*
		 * Permissions boosts initialization
		 */
		List<String> permsBoostsList = getList("permissions-boosts");
		
		if(permsBoostsList != null && !permsBoostsList.isEmpty()) {
			permsBoostsList.parallelStream().forEach(permBoost -> {
				try {
					String[] parts = permBoost.split(";");
					String permission = parts[0];
					float boost = Float.parseFloat(parts[1]);
					this.permissionsBoosts.put(permission, boost);
				} catch (Exception e) {
					logger.severe("Failed load boost for permission '" + permBoost + "': " + e.getMessage());
				}
			});
		}
		
		/*
		 * Materials boosts initialization
		 */
		ConfigurationSection materialsBoosts = getFileConfig().getConfigurationSection("materials-boosts");
		Set<String> materialsBoostsKeys = materialsBoosts != null ? materialsBoosts.getKeys(false) : null;
		
		if(materialsBoostsKeys != null && !materialsBoostsKeys.isEmpty()) {
			materialsBoostsKeys.parallelStream().forEach(key -> {
				try {
					Material material = Material.valueOf(key.toUpperCase());
					if(material == null)
						throw new JobConfigLoadException(jobType, "Unknown materials type");
					
					float boost = (float) materialsBoosts.getDouble(key);
					this.materialsBoosts.put(material, boost);
				} catch (Exception e) {
					logger.severe("Failed load boost for tile '" + key + "': " + e.getMessage());
				}
			});
		}
		
		/*
		 * Tasks parameters initialization
		 */
		this.useTask = getBoolean("use-child-task", false);
		this.defaultTaskPeriod = getInt("default-task-period", 10);
		
		String replacementstr = getString("replacement", "air");
		this.replacement = Material.valueOf(replacementstr.toUpperCase());
		
		if(this.replacement == null)
			throw new JobConfigLoadException(jobType, "Unknown replacement material '" + replacementstr + "'");
		
		/*
		 * Custom tasks periods initialization
		 */
		ConfigurationSection customTasks = getFileConfig().getConfigurationSection("custom-task-periods");
		Set<String> customTaskPeriods = customTasks != null ? customTasks.getKeys(false) : null;
		
		if(customTaskPeriods != null && !customTaskPeriods.isEmpty()) {
			for(String key : customTaskPeriods) {
				try {
					Material material = Material.valueOf(key.toUpperCase());
					if(material == null)
						throw new JobConfigLoadException(jobType, "Failed load custom task period for '" + key + "'");
					
					int period = customTasks.getInt(key);
					this.taskPeriods.put(material, period);
				} catch (Exception e) {
					throw new JobConfigLoadException(jobType, "Failed load custom task period for '" + key + "'");
				}
			}
		}
		
		/*
		 * Blocks types initialization
		 */
		this.useBlocktypes = getBoolean("use-blocktypes", false);
		if(useBlocktypes) {
			List<String> blocktypes = getList("blocktypes");
			if(blocktypes != null && !blocktypes.isEmpty())
				this.blocktypes = blocktypes.parallelStream()
						.map(m -> Material.valueOf(m.toUpperCase()))
						.filter(m -> m != null)
						.collect(Collectors.toList());
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

	/*
	 * Boosts
	 */
	
	public boolean hasBoost(Enum tile) {
		return materialsBoosts.containsKey(tile);
	}

	public boolean hasBoost(Player player) {
		for(String p : permissionsBoosts.keySet())
			if(player.hasPermission(p)) return true;
		return false;
	}

	public float getBoost(Material material) {
		return materialsBoosts.containsKey(material) ? materialsBoosts.get(material) : 1.0f;
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
	
	/*
	 * Task period
	 */
	
	public int getTaskPeriod(Material material) {
		return taskPeriods.containsKey(material) ? taskPeriods.get(material) : defaultTaskPeriod;
	}
	
}
