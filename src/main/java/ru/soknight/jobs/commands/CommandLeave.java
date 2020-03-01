package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobProfile;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;

public class CommandLeave extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandLeave(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "jobs.leave", 2);
		this.sender = sender;
		this.args = args;
		this.dbm = dbm;
	}
	
	@Override
	public void execute() throws NotLoadedConfigException {
		if(!isPlayerRequired()) return;
		if(!hasPermission()) return;
		if(!isCorrectUsage()) return;
		
		String jobName = args[1];
		if(!isJobExist(jobName)) return;
		
		String name = sender.getName();
		JobType job = JobType.valueOf(args[1].toUpperCase());
		
		String jname = Config.getJobConfig(job).getName();
		JobProfile profile = dbm.getJobProfile(name, job);
		
		if(!dbm.isWorker(name) || profile == null) {
			sender.sendMessage(Messages.formatMessage("leave-failed", "%job%", jname));
			return;
		}
		
		dbm.removeJobProfile(profile);
		
		sender.sendMessage(Messages.formatMessage("leaved", "%job%", jname));
	}
	
}
