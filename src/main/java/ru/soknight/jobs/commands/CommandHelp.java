package ru.soknight.jobs.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.HelpMessageFactory;
import ru.soknight.jobs.handlers.HelpMessageFactory.Node;

public class CommandHelp extends AbstractSubCommand {

	private final CommandSender sender;
	
	public CommandHelp(CommandSender sender) {
		super(sender, null, "jobs.help", 1);
		this.sender = sender;
	}

	@Override
	public void execute() {
		if(!hasPermission()) return;
		
		String header = Messages.getRawMessage("help-header");
		String footer = Messages.getRawMessage("help-footer");
		
		HelpMessageFactory hmf = new HelpMessageFactory(sender);
		
		hmf.addHelpMessage("help", "jobs.help", "help");
		hmf.addHelpMessage("join", "jobs.join", "join", Node.JOB);
		hmf.addHelpMessage("leave", "jobs.leave", "leave", Node.JOB);
		hmf.addHelpMessage("info", "jobs.info", "info", Node.JOB);
		hmf.addHelpMessage("list", "jobs.list.workers", "list");
		hmf.addHelpMessage("start", "jobs.work.start", "start", Node.JOB);
		hmf.addHelpMessage("done", "jobs.work.done", "done");
		hmf.addHelpMessage("edit", "jobs.selection.edit", "edit", Node.JOB);
		hmf.addHelpMessage("finish", "jobs.selection.finish", "finish");
		hmf.addHelpMessage("listtypes", "jobs.list.types", "listtypes", Node.JOB, Node.PAGE);
		hmf.addHelpMessage("listblocks", "jobs.list.blocks", "listblocks", Node.JOB, Node.PAGE);
		hmf.addHelpMessage("addblocktype", "jobs.blocktypes.add", "addblocktype", Node.JOB, Node.MATERIAL);
		hmf.addHelpMessage("remblocktype", "jobs.blocktypes.remove", "remblocktype", Node.JOB, Node.MATERIAL);
		hmf.addHelpMessage("reload", "jobs.reload", "reload");
		
		List<String> body = hmf.create();
		
		sender.sendMessage(header);
		body.forEach(str -> sender.sendMessage(str));
		sender.sendMessage(footer);
		return;
	}
	
}
