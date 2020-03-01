package ru.soknight.jobs;

import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobProfile;
import ru.soknight.jobs.database.WorkerProfile;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.objects.JobConfig;

@RequiredArgsConstructor
public class JobsExpansion extends PlaceholderExpansion {

	private final Jobs plugin;
	private final DatabaseManager databaseManager;
	
	@Override
	public String getAuthor() {
		return "SoKnight";
	}

	@Override
	public String getIdentifier() {
		return "jobs";
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
	
	@Override
    public String onPlaceholderRequest(Player p, String id){
        if(p == null) return "";

        String[] parts = id.split("_");
        String name = p.getName();
        
        if(parts.length > 1) {
        	String joblower = parts[0];
        	
        	JobType job = JobType.valueOf(joblower.toUpperCase());
        	if(job == null) return "";
            
            if(parts.length < 2) return "";
            String subid = parts[1];
            
        	switch (subid) {
        	case "joined": {
        		if(!databaseManager.isWorker(name)) return "false";
        		return String.valueOf(databaseManager.hasJobProfile(name, job));
        	}
        	case "worknow": {
        		WorkerProfile profile = databaseManager.getProfile(name);
        		if(profile == null) return "false";
        		
        		JobType current = profile.getCurrentJob();
        		if(current == null) return "false";
        		
        		return String.valueOf(current.equals(job));
        	}
        	case "level": {
        		if(!databaseManager.isWorker(name)) return "1";
        		
        		JobProfile jobProfile = databaseManager.getJobProfile(name, job);
        		if(jobProfile == null) return "1";
        		
        		return String.valueOf(jobProfile.getLevel());
        	}
        	case "progress": {
        		if(!databaseManager.isWorker(name)) return "0";
        		
        		JobProfile jobProfile = databaseManager.getJobProfile(name, job);
        		if(jobProfile == null) return "0";
        		
        		return String.valueOf(jobProfile.getProgress());
        	}
        	case "needed": {
        		if(!databaseManager.isWorker(name)) return "0";
        		
        		JobProfile jobProfile = databaseManager.getJobProfile(name, job);
        		if(jobProfile == null) return "0";
        		
				try {
					int needed = jobProfile.getNeeded();
					return String.valueOf(needed);
				} catch (NotLoadedConfigException e) {
					return "0";
				}
        	}
        	case "permboost": {
				try {
					JobConfig config = Config.getJobConfig(job);
					return String.valueOf(config.getBoost(p));
				} catch (NotLoadedConfigException e) {
					return "1.0";
				}
        	}
        	case "basesalary": {
				try {
					JobConfig config = Config.getJobConfig(job);
					return String.valueOf(config.getBaseSalary());
				} catch (NotLoadedConfigException e) {
					return "1.0";
				}
        	}
        	default:
        		break;
        	}
        } else {
        	switch (id) {
			case "worknow": {
				WorkerProfile profile = databaseManager.getProfile(name);
        		if(profile == null) return "false";
				
				return String.valueOf(profile.getCurrentJob() != null);
			}
			default:
				break;
			}
        }
        
        return null;
    }

}
