package ru.soknight.jobs.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;
import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.JobConfigLoadException;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.objects.JobConfig;
import ru.soknight.jobs.utils.Logger;

public class Config {

	@Getter private static FileConfiguration config;
	@Getter private static String prefix;
	@Getter private static boolean usePrefix;
	
	private static Map<JobType, JobConfig> jobConfigs;
	
	public static void refresh() {
		Jobs instance = Jobs.getInstance();
		
		File datafolder = instance.getDataFolder();
		if(!datafolder.isDirectory()) datafolder.mkdirs();
		
		File file = new File(datafolder, "config.yml");
		if(!file.exists()) {
			try {
				Files.copy(instance.getResource("config.yml"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Logger.info("Generated new config file.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		config = YamlConfiguration.loadConfiguration(file);
		
		usePrefix = config.getBoolean("messages.use-prefix");
		if(usePrefix) prefix = config.getString("messages.prefix").replace("&", "\u00A7");
		
		reloadJobConfigs(instance, datafolder);
	}
	
	public static void reloadJobConfigs(Jobs instance, File datafolder) {
		jobConfigs = new HashMap<>();
		
		File configsfolder = new File(datafolder, "jobs");
		if(!configsfolder.isDirectory()) configsfolder.mkdirs();
		
		for(JobType job : JobType.values()) {
			try {
				String name = job.name().toLowerCase() + ".yml";
				File file = new File(configsfolder, name);
				
				if(!file.exists()) {
					InputStream input = instance.getResource("jobs" + File.separator + name);
					Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				JobConfig jobConfig = new JobConfig(job, config);
				
				jobConfigs.put(job, jobConfig);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JobConfigLoadException e) {
				Logger.warning("Skipped loading configuration for " + job.name().toLowerCase() + ": "
						+ e.getMessage());
			}
		}
		
		Logger.info("Initialized " + jobConfigs.size() + " jobs configurations.");
	}
	
	public static JobConfig getJobConfig(JobType job) throws NotLoadedConfigException {
		JobConfig config = jobConfigs.get(job);
		if(config == null)
			throw new NotLoadedConfigException(job.name().toLowerCase());
		
		return config;
	}
	
	public static List<String> getStringList(String section) {
		List<String> output = new ArrayList<>();
		for(String s : config.getStringList(section))
			output.add(s.replace("&", "\u00A7"));
		return output;
	}
	
}
