package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;

public class CommandReload {

	public static void execute(CommandSender sender) {
		Config.refresh();
		Messages.refresh();
		sender.sendMessage(Messages.getMessage("reload-success"));
		return;
	}
	
}
