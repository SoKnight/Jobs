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

public class CommandDone extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final DatabaseManager dbm;
	
	public CommandDone(CommandSender sender, DatabaseManager dbm) {
		super(sender, null, "jobs.work.done", 1);
		this.sender = sender;
		this.dbm = dbm;
	}
	
	@Override
	public void execute() throws NotLoadedConfigException {
		if(!isPlayerRequired()) return;
		if(!hasPermission()) return;
		
		Player p = (Player) sender;
		String name = p.getName();
		
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null || worker.getCurrentJob() == null) {
			p.sendMessage(Messages.getMessage("work-done-failed"));
			return;
		}
		
		JobType job = worker.getCurrentJob();
		worker.setCurrentJob(null);
		dbm.updateProfile(worker);
		WorkStatusSwitcher.done(p);
		
		String jname = Config.getJobConfig(job).getName();
		p.sendMessage(Messages.formatMessage("work-doned", "%job%", jname));
	}
	
}
