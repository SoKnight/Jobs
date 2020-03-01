package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.SessionManager;

public class CommandFinish extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final SessionManager sm;
	
	public CommandFinish(CommandSender sender, SessionManager sm) {
		super(sender, null, "jobs.selection.finish", 1);
		this.sender = sender;
		this.sm = sm;
	}

	@Override
	public void execute() throws NotLoadedConfigException {
		if(!isPlayerRequired()) return;
		if(!hasPermission()) return;
		
		Player p = (Player) sender;
		
		String name = p.getName();
		if(!sm.hasSession(name)) {
			p.sendMessage(Messages.getMessage("edit-finish-failed"));
			return;
		}
		
		JobType job = sm.getSession(name).getJob();
		sm.doneSession(name);
		
		String jname = Config.getJobConfig(job).getName();
		p.sendMessage(Messages.formatMessage("edit-finished", "%job%", jname));
	}
	
}
