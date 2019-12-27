package ru.soknight.jobs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Messages;

public class CommandList {
	
	private static String[] jobs = new String[] {"MINER", "WOODCUTTER", "HUNTER", "FISHERMAN", "FARMER"};
	
	public static void execute(CommandSender sender) {
		String header = Messages.getRawMessage("workers-header"), 
			   body = Messages.getRawMessage("workers-body"),
			   footer = Messages.getRawMessage("workers-footer");
		
		List<String> output = new ArrayList<>();
		for(String j : jobs) {
			JobType job = JobType.valueOf(j);
			String jobname = job.getName();
			String workers = String.valueOf(DatabaseManager.getWorkersCount(job));
			output.add(body.replace("%job%", jobname).replace("%workers%", workers));
		}
		
		sender.sendMessage(header);
		output.forEach(s -> sender.sendMessage(s));
		sender.sendMessage(footer);
	}
	
}
