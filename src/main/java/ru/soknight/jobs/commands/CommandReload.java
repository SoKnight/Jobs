package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;

public class CommandReload extends AbstractSubCommand {
	
	private final CommandSender sender;

	public CommandReload(CommandSender sender) {
		super(sender, null, "jobs.reload", 1);
		this.sender = sender;
	}

	@Override
	public void execute() {
		if(!hasPermission()) return;
		
		Config.refresh();
		Messages.refresh();
		Jobs.getInstance().getSessionManager().restart();
		
		sender.sendMessage(Messages.getMessage("reload-success"));
	}
	
}
