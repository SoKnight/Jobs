package ru.soknight.jobs.command;

import org.bukkit.command.CommandSender;

import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.command.help.HelpMessage;
import ru.soknight.lib.command.help.HelpMessageFactory;
import ru.soknight.lib.command.help.HelpMessageItem;
import ru.soknight.lib.command.placeholder.Placeholder;
import ru.soknight.lib.command.placeholder.SimplePlaceholder;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.validator.PermissionValidator;

public class CommandHelp extends ExtendedSubcommandExecutor {

	private final HelpMessage message;
	
	public CommandHelp(Messages messages) {
		super(messages);
		
		HelpMessageFactory factory = new HelpMessageFactory(messages, "jobs.%command%");
		
		Placeholder pjob = new SimplePlaceholder(messages, "job");
		Placeholder pjobopt = new SimplePlaceholder(messages, "job-opt");
		Placeholder pplayer = new SimplePlaceholder(messages, "player-opt");
		Placeholder pselection = new SimplePlaceholder(messages, "selection");
		Placeholder ppage = new SimplePlaceholder(messages, "page");
		
		HelpMessageItem help = new HelpMessageItem("help", messages);
		HelpMessageItem join = new HelpMessageItem("join", messages, pjob);
		HelpMessageItem leave = new HelpMessageItem("leave", messages, pjob);
		HelpMessageItem info = new HelpMessageItem("info", messages, pjob, pplayer);
		HelpMessageItem list = new HelpMessageItem("list", messages);
		HelpMessageItem start = new HelpMessageItem("start", messages, pjob);
		HelpMessageItem done = new HelpMessageItem("done", messages);
		HelpMessageItem listblocks = new HelpMessageItem("listblocks", messages, pjob, ppage);
		HelpMessageItem selection = new HelpMessageItem("selection", messages, pselection, pjobopt);
		HelpMessageItem reload = new HelpMessageItem("reload", messages);
		
		start.setPermission("jobs.work");
		done.setPermission("jobs.work");
		listblocks.setPermission("jobs.list.blocks");
		
		factory.appendItems(true, help, join, leave, info, list, start, done, listblocks, selection, reload);
		
		this.message = factory.build();
		
		String permmsg = messages.get("error.no-permissions");
		
		PermissionValidator permval = new PermissionValidator("jobs.help", permmsg);
		
		super.addValidators(permval);
	}

	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		if(!validateExecution(sender, args)) return;
		
		message.send(sender);
	}
	
}
