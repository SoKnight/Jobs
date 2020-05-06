package ru.soknight.jobs.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.command.validation.JobExecutionData;
import ru.soknight.jobs.command.validation.JobValidator;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.SessionManager;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.ValidationResult;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.SenderIsPlayerValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandSelection extends ExtendedSubcommandExecutor {
	
	private final Config config;
	private final Messages messages;
	private final SessionManager sessionManager;
	
	public CommandSelection(Config config, Messages messages, SessionManager sessionManager) {
		super(messages);
		
		this.config = config;
		this.messages = messages;
		this.sessionManager = sessionManager;
		
		String sendermsg = messages.get("error.only-for-players");
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		
		Validator senderval = new SenderIsPlayerValidator(sendermsg);
		Validator permval = new PermissionValidator("jobs.selection", permmsg);
		Validator argsval = new ArgsCountValidator(1, argsmsg);
		
		super.addValidators(senderval, permval, argsval);
	}
	
	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		if(!validateExecution(sender, args)) return;
		
		String action = args.get(0);
		if(action.equalsIgnoreCase("start"))
			executeStart(sender, args);
		else if(action.equalsIgnoreCase("done"))
			executeDone(sender);
		else
			messages.sendFormatted(sender, "selection.unknown-action", "%action%", action);
	}
	
	private void executeStart(CommandSender sender, CommandArguments args) {
		if(args.size() < 2) {
			messages.getAndSend(sender, "error.wrong-syntax");
			return;
		}
		
		String jobName = args.get(1);
		
		Validator validator = new JobValidator(config, messages);
		CommandExecutionData data = new JobExecutionData(sender, args, jobName);
		ValidationResult result = validator.validate(data);
		
		if(!result.isPassed()) {
			messages.send(sender, result.getMessage());
			return;
		}
		
		JobTypeEnum job = JobTypeEnum.valueOf(jobName.toUpperCase());
		
		String name = sender.getName();
		if(sessionManager.hasSession(name)) {
			messages.getAndSend(sender, "selection.start.failed");
			return;
		}
		
		sessionManager.startSession(name, job);
		
		String jname = this.config.getJobConfig(job).getName();
		messages.sendFormatted(sender, "selection.start.success", "%job%", jname);
	}
	
	private void executeDone(CommandSender sender) {
		String name = sender.getName();
		if(!sessionManager.hasSession(name)) {
			messages.getAndSend(sender, "selection.finish.failed");
			return;
		}
		
		JobTypeEnum job = sessionManager.getSessionJob(name);
		sessionManager.doneSession(name);
		
		JobConfiguration config = this.config.getJobConfig(job);
		String jname = config != null && config.isInitialized() ? config.getName() : job.getDefaultName();
		
		messages.sendFormatted(sender, "selection.finish.success", "%job%", jname);
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, CommandArguments args) {
		if(args.isEmpty() || args.size() > 2 || !validateTabCompletion(sender, args)) return null;
		
		if(args.size() == 1) {
			String arg = args.get(0).toLowerCase();
			
			List<String> matches = new ArrayList<>();
			
			if("start".startsWith(arg))
				matches.add("start");
			
			if("done".startsWith(arg))
				matches.add("done");
			
			return matches;
		} else if(args.get(0).equalsIgnoreCase("start")) {
			String arg = args.get(1).toLowerCase();
			
			List<String> matches = Arrays.asList(JobTypeEnum.values()).stream()
					.map(j -> j.toString().toLowerCase())
					.filter(j -> j.startsWith(arg))
					.collect(Collectors.toList());
			
			return matches;
		} else return null;
	}
	
}
