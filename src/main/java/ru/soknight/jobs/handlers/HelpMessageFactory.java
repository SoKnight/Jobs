package ru.soknight.jobs.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.files.Messages;

public class HelpMessageFactory {

	private static Map<String, String> cache = new HashMap<>();
	
	private final CommandSender sender;
	private List<String> messages;
	
	public HelpMessageFactory(CommandSender sender) {
		this.sender = sender;
		this.messages = new ArrayList<>();
	}
	
	public void addHelpMessage(String command, String permission, String description) {
		this.addHelpMessage(command, permission, description, (Node[]) null);
	}
	
	public void addHelpMessage(String command, String permission, String description, Node... nodes) {
		if(!sender.hasPermission(permission)) return;
		
		if(cache.containsKey(description)) {
			messages.add(cache.get(description));
			return;
		}
		
		String description2 = Messages.getRawMessage("help-descriptions." + description);
		
		if(nodes != null && nodes.length != 0)
			for(Node node : nodes)
				command += " " + node.getNode();

		String message = Messages.formatRawMessage("help-body", "%command%", command, "%description%", description2);
		cache.put(description, message);
		messages.add(message);
	}
	
	public List<String> create() {
		return messages;
	}
	
	public enum Node {
		
		JOB, NAME, MATERIAL, PAGE;
		
		public String getNode() {
			return Messages.getRawMessage("help-nodes." + name().toLowerCase());
		}
		
	}
	
}
