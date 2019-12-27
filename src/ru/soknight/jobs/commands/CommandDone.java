package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.utils.Requirements;
import ru.soknight.jobs.utils.Utils;
import ru.soknight.jobs.utils.WorkStatusSwitcher;

public class CommandDone {
	
	public static void execute(CommandSender sender) {
		if(!Requirements.isPlayer(sender)) return;
		
		Player p = (Player) sender;
		String name = p.getName();
		if(!DatabaseManager.isWorkNow(name)) {
			p.sendMessage(Messages.getMessage("work-done-failed"));
			return; }
		
		JobType job = DatabaseManager.getCurrentJob(name);
		DatabaseManager.resetCurrentJob(name);
		WorkStatusSwitcher.done(p, job);
		p.sendMessage(Utils.pformat("work-doned", "job@@" + job.getName()));
		return;
	}
	
}
