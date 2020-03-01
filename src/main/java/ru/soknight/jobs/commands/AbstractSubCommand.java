package ru.soknight.jobs.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.files.Messages;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class AbstractSubCommand implements ISubCommand {

	private final CommandSender sender;
	
	private String[] args;
	private String permission;
    private int minArgsLength = 0;
    
    @Override
    public boolean isCorrectUsage() {
    	if(args.length >= minArgsLength) return true;
    		
    	sender.sendMessage(Messages.getMessage("error-wrong-syntax"));
    	return false;
    }

    @Override
    public boolean hasPermission() {
    	if(sender.hasPermission(permission)) return true;
    	
    	sender.sendMessage(Messages.getMessage("error-no-permissions"));
        return true;
    }
    
    @Override
    public boolean hasPermission(String permission) {
    	if(sender.hasPermission(permission)) return true;
    	
    	sender.sendMessage(Messages.getMessage("error-no-permissions"));
        return true;
    }
    
    @Override
    public boolean isPlayerRequired() {
    	if(sender instanceof Player) return true;
    	
    	sender.sendMessage(Messages.getMessage("error-only-for-players"));
    	return false;
    }
    
    @Override
    public boolean isPlayerExist(String name) {
    	for(Player p : Bukkit.getOnlinePlayers())
    		if(p.getName().equals(name)) return true;
    	for(OfflinePlayer op : Bukkit.getOfflinePlayers())
    		if(op.getName().equals(name)) return true;
    	
    	sender.sendMessage(Messages.formatMessage("error-player-not-found", "%name%", name));
    	return false;
    }
    
    @Override
    public boolean isJobExist(String job) {
    	JobType jobType = JobType.valueOf(job.toUpperCase());
    	if(jobType != null) return true;
    	
    	sender.sendMessage(Messages.formatMessage("error-unknown-job", "%job%", job));
    	return false;
    }
    
    @Override
    public boolean workOnJob(String name, JobType job) {
    	DatabaseManager dbm = Jobs.getInstance().getDatabaseManager();
    	if(dbm.hasJobProfile(name, job)) return true;
    	
    	sender.sendMessage(Messages.getMessage("error-dont-work"));
    	return false;
    }
    
    @Override
    public boolean argIsInteger(String arg) {
    	try {
			Integer.parseInt(arg);
			return true;
		} catch (NumberFormatException ignored) {
			sender.sendMessage(Messages.formatMessage("error-arg-is-not-int", "%arg%", arg));
			return false;
		}
    }
	
}
