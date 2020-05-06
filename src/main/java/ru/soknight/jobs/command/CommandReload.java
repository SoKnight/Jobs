package ru.soknight.jobs.command;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.Jobs;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandReload extends ExtendedSubcommandExecutor {
	
	private final Jobs plugin;
	private final Messages messages;
	
	public CommandReload(Jobs plugin, Messages messages) {
		super(messages);
		
		this.plugin = plugin;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		
		Validator permval = new PermissionValidator("jobs.reload", permmsg);
		
		super.addValidators(permval);
	}
	
	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		if(!validateExecution(sender, args)) return;
		
		plugin.refresh();
		
		messages.getAndSend(sender, "reload-success");
	}
	
}
