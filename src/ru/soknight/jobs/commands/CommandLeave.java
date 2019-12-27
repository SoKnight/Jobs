package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.utils.Requirements;
import ru.soknight.jobs.utils.Utils;

public class CommandLeave {

	public static void execute(CommandSender sender, String[] args) {
		if(Requirements.isInvalidUsage(sender, args, 2)) return;
		if(!Requirements.jobExist(sender, args[1])) return;
		
		JobType job = JobType.valueOf(args[1].toUpperCase());
		String name = sender.getName();
		if(args.length > 2)
			if(!Requirements.playerExist(sender, args[2])) return;
			else name = args[2];
		else if(!Requirements.isPlayer(sender)) return;
		
		boolean isPlayer = sender instanceof Player;
		String jobname = job.getName();
		
		if(DatabaseManager.leave(name, job)) {
			if(isPlayer) sender.sendMessage(Utils.pformat("leave-success", "job@@" + jobname));
			else sender.sendMessage(Utils.pformat("leave-other-success", "job@@" + jobname, "player@@" + name));
		} else {
			if(isPlayer) sender.sendMessage(Utils.pformat("leave-failed-not-joined", "job@@" + jobname));
			else sender.sendMessage(Utils.pformat("leave-other-failed-not-joined", "job@@" + jobname, "player@@" + name));
		}
		return;
	}
	
}
