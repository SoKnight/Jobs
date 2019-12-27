package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobProfile;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.database.WorkerProfile;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.utils.Requirements;
import ru.soknight.jobs.utils.Utils;

public class CommandJoin {
	
	public static void execute(CommandSender sender, String[] args) {
		if(Requirements.isInvalidUsage(sender, args, 2)) return;
		if(!Requirements.jobExist(sender, args[1])) return;
		
		// Getting name and job
		JobType job = JobType.valueOf(args[1].toUpperCase());
		String name = sender.getName();
		if(args.length > 2)
			if(!Requirements.playerExist(sender, args[2])) return;
			else name = args[2];
		else if(!Requirements.isPlayer(sender)) return;
		
		// Checking for job limit
		if(DatabaseManager.isWorker(name)) {
			WorkerProfile profile = DatabaseManager.getProfile(name);
			if(profile.getJobCount() >= Config.config.getInt("misc.max-jobs")) {
				sender.sendMessage(Messages.getMessage("join-failed-max"));
				return; }	
		}
		
		// Creating job profile
		boolean isPlayer = sender instanceof Player;
		String jobname = job.getName();
		JobProfile profile = new JobProfile(name, job);
	
		// Joining
		String message;
		if(DatabaseManager.join(name, profile)) {
			if(isPlayer) message = Utils.pformat("join-success", "job@@" + jobname);
			else message = Utils.pformat("join-other-success", "job@@" + jobname, "player@@" + name);
		} else {
			if(isPlayer) message = Utils.pformat("join-failed-success", "job@@" + jobname);
			else message = Utils.pformat("join-other-failed-already", "job@@" + jobname, "player@@" + name);
		}
		
		sender.sendMessage(message);
		return;
	}
	
}
