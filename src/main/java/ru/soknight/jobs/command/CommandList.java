package ru.soknight.jobs.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.profile.ProfilesManager;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandList extends ExtendedSubcommandExecutor {
	
	private final Config config;
	private final Messages messages;
	private final ProfilesManager profilesManager;
	
	private final String header;
	private final String body;
	private final String footer;
	
	public CommandList(Config config, Messages messages, ProfilesManager profilesManager) {
		super(messages);
		
		this.config = config;
		this.messages = messages;
		this.profilesManager = profilesManager;
		
		this.header = messages.get("list.employees.header");
		this.body = messages.get("list.employees.body");
		this.footer = messages.get("list.employees.footer");
		
		String permmsg = messages.get("error.no-permissions");
		
		Validator permval = new PermissionValidator("jobs.list", permmsg);
		
		super.addValidators(permval);
	}
	
	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		if(!validateExecution(sender, args)) return;
		
		long total = profilesManager.getTotalEmployeesCount();
		String header = messages.format(this.header, "%total%", total);
		
		List<String> body = new ArrayList<>();
		Arrays.stream(JobTypeEnum.values()).forEach(job -> {
			JobConfiguration config = this.config.getJobConfig(job);
			if(config == null || !config.isInitialized())
				return;
			
			String jname = config.getName();
			long employeesCount = profilesManager.getEmployeesCount(job);
			
			body.add(messages.format(this.body,
					"%job%", jname,
					"%employees%", employeesCount));
		});
		
		messages.send(sender, header);
		body.forEach(b -> messages.send(sender, b));
		messages.send(sender, this.footer);
	}
	
}
