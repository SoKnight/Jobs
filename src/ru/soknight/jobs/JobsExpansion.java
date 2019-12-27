package ru.soknight.jobs;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobType;

public class JobsExpansion extends PlaceholderExpansion {

	private Jobs plugin;
	
	public JobsExpansion(Jobs plugin) {
		this.plugin = plugin;
	}
	
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
	
	private static final List<String> jobs = Arrays.asList("miner", "woodcutter", "hunter", "farmer", "fisherman");
	
	@Override
    public String onPlaceholderRequest(Player p, String id){
        if(p == null) return "";

        String[] parts = id.split("_");
        String name = p.getName();
        
        if(parts.length > 1) {
        	String joblower = parts[0];
        	
        	if(!jobs.contains(joblower)) return "";
            JobType job = JobType.valueOf(joblower.toUpperCase());
            
            if(parts.length < 2) return "";
            String subid = parts[1];
            
        	switch (subid) {
        	case "joined": {
        		return String.valueOf(DatabaseManager.hasJobProfile(name, job));
        	}
        	case "worknow": {
        		return String.valueOf(DatabaseManager.getCurrentJob(name).equals(job));
        	}
        	case "level": {
        		if(!DatabaseManager.hasJobProfile(name, job)) return "1";
        		else return String.valueOf(DatabaseManager.getJobProfile(name, job).getLevel());
        	}
        	case "progress": {
        		if(!DatabaseManager.hasJobProfile(name, job)) return "0";
        		else return String.valueOf(DatabaseManager.getJobProfile(name, job).getProgress());
        	}
        	case "needed": {
        		if(!DatabaseManager.hasJobProfile(name, job)) return "0";
        		else return String.valueOf(DatabaseManager.getJobProfile(name, job).getNeeded());
        	}
        	case "permboost": {
        		return String.valueOf(DatabaseManager.getJobInfo(job).getBoost(p));
        	}
        	case "basesalary": {
        		return String.valueOf(DatabaseManager.getJobInfo(job).getBaseSalary());
        	}
        	default:
        		break;
        	}
        } else {
        	switch (id) {
			case "worknow": {
				return String.valueOf(DatabaseManager.isWorkNow(name));
			}
			default:
				break;
			}
        }
        
        return null;
    }

}
