package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.SessionManager;

public class CommandEdit extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final SessionManager sm;
	
	public CommandEdit(CommandSender sender, String[] args, SessionManager sm) {
		super(sender, args, "jobs.selection.edit", 2);
		this.sender = sender;
		this.args = args;
		this.sm = sm;
	}

	@Override
	public void execute() throws NotLoadedConfigException {
		if(!isPlayerRequired()) return;
		if(!hasPermission()) return;
		if(!isCorrectUsage()) return;
		
		String jobName = args[1];
		if(!isJobExist(jobName)) return;
		
		Player p = (Player) sender;
		JobType job = JobType.valueOf(jobName.toUpperCase());
		
		String name = p.getName();
		if(sm.hasSession(name)) {
			p.sendMessage(Messages.getMessage("edit-start-failed"));
			return;
		}
		
		sm.startSession(name, job);
		
		String jname = Config.getJobConfig(job).getName();
		p.sendMessage(Messages.formatMessage("edit-started", "%job%", jname));
	}
	
}
