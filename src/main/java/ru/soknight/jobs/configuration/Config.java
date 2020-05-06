package ru.soknight.jobs.configuration;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.exception.JobConfigLoadException;
import ru.soknight.lib.configuration.AbstractConfiguration;

public class Config extends AbstractConfiguration {

	private final Map<JobTypeEnum, JobConfiguration> jobsConfigurations;
	
	public Config(Jobs plugin) {
		super(plugin, "config.yml");
		
		super.refresh();
		
		/*
		 * Loading manual jobs configurations
		 */
		
		this.jobsConfigurations = new ConcurrentHashMap<>();
		
		loadJobsConfiguration(plugin);
	}
	
	public void loadJobsConfiguration(Jobs plugin) {
		this.jobsConfigurations.clear();
		
		File datafolder = new File(plugin.getDataFolder(), "jobs");
		
		Arrays.stream(JobTypeEnum.values()).parallel().forEach(job -> {
			String filename = job.toString().toLowerCase() + ".yml";
			InputStream source = plugin.getClass().getResourceAsStream("/jobs/" + filename);
			
			try {
				JobConfiguration config = new JobConfiguration(plugin, datafolder, source, job);
				this.jobsConfigurations.put(job, config);
			} catch (JobConfigLoadException e) {
				plugin.getLogger().severe("Failed to load " + job.toString().toLowerCase() + ": " + e.getMessage());
			}
		});
	}
	
	public JobConfiguration getJobConfig(JobTypeEnum job) {
		return this.jobsConfigurations.get(job);
	}
	
}
