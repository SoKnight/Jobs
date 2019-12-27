package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.utils.Requirements;
import ru.soknight.jobs.utils.Utils;
import ru.soknight.jobs.utils.WorkStatusSwitcher;

public class CommandStart {
	
	public static void execute(CommandSender sender, String[] args) {
		if(!Requirements.isPlayer(sender)) return;
		if(Requirements.isInvalidUsage(sender, args, 2)) return;
		if(!Requirements.jobExist(sender, args[1])) return;
		
		Player p = (Player) sender;
		String name = p.getName();
		if(DatabaseManager.isWorkNow(name)) {
			p.sendMessage(Messages.getMessage("work-start-failed-already"));
			return; }
		
		JobType job = JobType.valueOf(args[1].toUpperCase());
		if(!DatabaseManager.hasJobProfile(name, job)) {
			p.sendMessage(Utils.pformat("work-start-failed-not-joined", "job@@" + job.getName()));
			return; }
		WorkStatusSwitcher.start(p, job);
		DatabaseManager.setCurrentJob(name, job);
		
		p.sendMessage(Utils.pformat("work-started", "job@@" + job.getName()));
		return;
	}
	
}
