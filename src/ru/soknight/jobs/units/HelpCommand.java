package ru.soknight.jobs.units;

import ru.soknight.jobs.files.Messages;

public class HelpCommand {

	private String format, command, description;
	
	public HelpCommand(String format, String command, String description) {
		this.format = format;
		this.command = command;
		this.description = description;
	}
	
	public HelpCommand(String format, String command, String description, String... args) {
		this.format = format;
		this.command = command;
		this.description = description;
		for(String a : args)
			this.command += " " + Messages.getMessage("help-nodes." + a);
	}
	
	@Override
	public String toString() {
		return format.replace("%cmd%", command).replace("%desc%", description);
	}
	
}
