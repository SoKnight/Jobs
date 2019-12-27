package ru.soknight.jobs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.files.Messages;

public class CommandHelp {

	public static void execute(CommandSender sender) {
		String header = Messages.getRawMessage("help-header");
		String footer = Messages.getRawMessage("help-footer");
		List<String> body = new ArrayList<>();
		
		body.add(Messages.getHelpString("help").toString());
		if(sender.hasPermission("jobs.addtool")) 		body.add(Messages.getHelpString("addtool").toString());
		if(sender.hasPermission("jobs.join")) 			body.add(Messages.getHelpString("join").toString());
		if(sender.hasPermission("jobs.join.other")) 	body.add(Messages.getHelpString("join-other").toString());
		if(sender.hasPermission("jobs.info")) 			body.add(Messages.getHelpString("info").toString());
		if(sender.hasPermission("jobs.info.other")) 	body.add(Messages.getHelpString("info-other").toString());
		if(sender.hasPermission("jobs.leave")) 			body.add(Messages.getHelpString("leave").toString());
		if(sender.hasPermission("jobs.leave.other")) 	body.add(Messages.getHelpString("leave-other").toString());
		if(sender.hasPermission("jobs.list")) 			body.add(Messages.getHelpString("list").toString());
		if(sender.hasPermission("jobs.reload")) 		body.add(Messages.getHelpString("reload").toString());
		if(sender.hasPermission("jobs.remtool")) 		body.add(Messages.getHelpString("remtool").toString());
		
		sender.sendMessage(header);
		body.forEach(str -> sender.sendMessage(str));
		sender.sendMessage(footer);
		return;
	}
	
}
