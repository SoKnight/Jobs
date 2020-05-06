package ru.soknight.jobs;

import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.profile.EmployeeProfile;
import ru.soknight.jobs.database.profile.PlayerProfile;
import ru.soknight.jobs.database.profile.ProfilesManager;

@RequiredArgsConstructor
public class JobsExpansion extends PlaceholderExpansion {

	private final Jobs plugin;
	private final Config config;
	private final ProfilesManager profilesManager;
	
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
    public String onPlaceholderRequest(Player p, String id) {
        if(p == null) return "";

        String[] parts = id.split("_");
        String name = p.getName();
        
        if(parts.length > 1) {
        	String joblower = parts[0];
        	
        	JobTypeEnum job = JobTypeEnum.valueOf(joblower.toUpperCase());
        	if(job == null) return "";
            
            if(parts.length < 2) return "";
            String subid = parts[1];
            
        	switch (subid) {
        	case "joined": {
        		if(!profilesManager.hasProfile(name))
        			return "false";
        		
        		return String.valueOf(profilesManager.hasEmployeeProfile(name, job));
        	}
        	case "worknow": {
        		PlayerProfile profile = profilesManager.getProfile(name);
        		if(profile == null) return "false";
        		
        		JobTypeEnum current = profile.getCurrentJob();
        		if(current == null) return "false";
        		
        		return String.valueOf(current.equals(job));
        	}
        	case "level": {
        		if(!profilesManager.hasProfile(name)) return "0";
        		
        		EmployeeProfile employeeProfile = profilesManager.getEmployeeProfile(name, job);
        		if(employeeProfile == null) return "0";
        		
        		return String.valueOf(employeeProfile.getLevel());
        	}
        	case "progress": {
        		if(!profilesManager.hasProfile(name)) return "0";
        		
        		EmployeeProfile employeeProfile = profilesManager.getEmployeeProfile(name, job);
        		if(employeeProfile == null) return "0";
        		
        		return String.valueOf(employeeProfile.getProgress());
        	}
        	case "needed": {
        		if(!profilesManager.hasProfile(name)) return "0";
        		
        		EmployeeProfile jobProfile = profilesManager.getEmployeeProfile(name, job);
        		if(jobProfile == null) return "0";
        		
        		JobConfiguration config = this.config.getJobConfig(job);
				if(config == null) return "0";
        		
				int needed = jobProfile.getNeeded(config);
				return String.valueOf(needed);
        	}
        	case "permboost": {
				JobConfiguration config = this.config.getJobConfig(job);
				if(config == null) return "1.0";
					
				return String.valueOf(config.getBoost(p));
        	}
        	case "basesalary": {
				JobConfiguration config = this.config.getJobConfig(job);
				if(config == null) return "1.0";
					
				return String.valueOf(config.getBaseSalary());
        	}
        	default:
        		break;
        	}
        } else {
        	switch (id) {
			case "worknow": {
				PlayerProfile profile = profilesManager.getProfile(name);
        		if(profile == null) return "false";
				
				return String.valueOf(profile.getCurrentJob() != null);
			}
			default:
				break;
			}
        }
        
        return "";
    }

}
