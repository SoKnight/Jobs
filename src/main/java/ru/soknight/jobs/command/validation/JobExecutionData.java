package ru.soknight.jobs.command.validation;

import org.bukkit.command.CommandSender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.validation.CommandExecutionData;

@Getter
@AllArgsConstructor
public class JobExecutionData implements CommandExecutionData {

	private final CommandSender sender;
	private final CommandArguments args;
	
	private final String jobName;
	
}
