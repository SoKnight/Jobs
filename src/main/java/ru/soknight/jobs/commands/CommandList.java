package ru.soknight.jobs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.utils.StringUtils;

public class CommandList extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final DatabaseManager dbm;
	
	public CommandList(CommandSender sender, DatabaseManager dbm) {
		super(sender, null, "jobs.list.workers", 1);
		this.sender = sender;
		this.dbm = dbm;
	}

	@Override
	public void execute() {
		if(!hasPermission()) return;
		
		int total = 0;
		String header = Messages.getRawMessage("list-header"),
			   body = Messages.getRawMessage("list-body"),
			   footer = Messages.getRawMessage("list-footer");
		
		List<String> output = new ArrayList<>();
		for(JobType job : JobType.values()) {
			String jname;
			try {
				jname = Config.getJobConfig(job).getName();
			} catch (NotLoadedConfigException e) {
				jname = "?";
			}
			int count = dbm.getWorkersCount(job);
			total += count;
			output.add(StringUtils.format(body, "%job%", jname, "%workers%", count));
		}
		
		sender.sendMessage(StringUtils.format(header, "%total%", total));
		output.forEach(s -> sender.sendMessage(s));
		sender.sendMessage(footer);
	}
	
}
