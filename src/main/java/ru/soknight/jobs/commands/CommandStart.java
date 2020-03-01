package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.WorkerProfile;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.WorkStatusSwitcher;

public class CommandStart extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandStart(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "jobs.work.start", 2);
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
		
		Player p = (Player) sender;
		String name = p.getName();
		
		JobType job = JobType.valueOf(jobName.toUpperCase());
		if(!workOnJob(name, job)) return;
		
		WorkerProfile worker = dbm.getProfile(name);
		if(worker != null && worker.getCurrentJob() != null) {
			p.sendMessage(Messages.getMessage("work-start-failed"));
			return;
		}
		
		if(!workOnJob(name, job)) return;
		
		WorkStatusSwitcher.start(p, job);
		
		worker.setCurrentJob(job);
		dbm.updateProfile(worker);
		
		String jname = Config.getJobConfig(job).getName();
		p.sendMessage(Messages.formatMessage("work-started", "%job%", jname));
	}
	
}
