package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobProfile;
import ru.soknight.jobs.database.WorkerProfile;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;

public class CommandJoin extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandJoin(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "jobs.join", 2);
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
			
		// Check for jobs limit
		WorkerProfile worker = dbm.getProfile(name);
		if(worker != null && worker.getJobCount() >= Config.getConfig().getInt("max-jobs")) {
			sender.sendMessage(Messages.getMessage("join-failed-max"));
			return;
		} else if(worker == null) {
			worker = new WorkerProfile(name);
			dbm.saveProfile(worker);
		}
		
		JobProfile profile = dbm.getJobProfile(jname, job);
		if(profile != null) {
			sender.sendMessage(Messages.formatMessage("join-failed-already", "%job%", jname));
			return;
		}
		
		profile = new JobProfile(name, job);
		dbm.saveJobProfile(profile);
		
		sender.sendMessage(Messages.formatMessage("joined", "%job%", jname));
	}
	
}
