package ru.soknight.jobs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobProfile;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.utils.Requirements;
import ru.soknight.jobs.utils.Utils;

public class CommandInfo {

	public static void execute(CommandSender sender, String[] args) {
		if(Requirements.isInvalidUsage(sender, args, 2)) return;
		if(!Requirements.jobExist(sender, args[1])) return;
		
		JobType job = JobType.valueOf(args[1].toUpperCase()); 
		String name = sender.getName(), jobname = job.getName();
		
		if(args.length > 2)
			if(!Requirements.playerExist(sender, args[2])) return;
			else name = args[2];
		else if(!Requirements.isPlayer(sender)) return;
		
		boolean isPlayer = sender instanceof Player;
		if(!DatabaseManager.hasJobProfile(name, job)) {
			if(isPlayer) sender.sendMessage(Utils.pformat("info-failed-not-joined", "job@@" + jobname));
			else sender.sendMessage(Utils.pformat("info-other-failed-not-joined", "job@@" + jobname, "player@@" + name));
			return;
		}
			
		// Getting values
		JobProfile profile = DatabaseManager.getJobProfile(name, job);
		
		int level = profile.getLevel(), exp = profile.getProgress(), needed = 0;
		if(level == Config.config.getInt("levels.max-levels." + job)) needed = Utils.getNeededExp(level);
		else needed = Utils.getNeededExp(level + 1);
		
		String boost = "Неизвестно";
		OfflinePlayer offpl = Bukkit.getOfflinePlayer(name);
		if(offpl.isOnline()) 
			boost = String.valueOf(DatabaseManager.getJobInfo(job).getBoost(offpl.getPlayer()));
		
		String lvlstr = String.valueOf(level), expstr = String.valueOf(exp), nddstr = String.valueOf(needed);
		String wcount = String.valueOf(DatabaseManager.getWorkersCount(job));
		
		if(level == DatabaseManager.getJobInfo(job).getMaxLevel()) lvlstr = "\u00A76" + lvlstr;
		
		// Adding messages
		List<String> output = new ArrayList<>();
		output.add(Messages.getRawMessage("info-header"));
		output.add(Utils.format("info-name", "name@@" + jobname));
		output.add(Utils.format("info-player", "nickname@@" + name));
		output.add(Utils.format("info-workers", "workers@@" + wcount));
		output.add(Utils.format("info-level", "level@@" + lvlstr, "exp@@" + expstr, "needed@@" + nddstr));
		output.add(Utils.format("info-boost", "bonus@@" + boost));
		output.add(Messages.getRawMessage("info-footer"));
		
		// Sending info to game chat
		output.forEach(s -> sender.sendMessage(s));
		return;
	}
	
}
