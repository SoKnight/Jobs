package ru.soknight.jobs.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.command.validation.JobExecutionData;
import ru.soknight.jobs.command.validation.JobValidator;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.profile.EmployeeProfile;
import ru.soknight.jobs.database.profile.PlayerProfile;
import ru.soknight.jobs.database.profile.ProfilesManager;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.SenderIsPlayerValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandJoin extends ExtendedSubcommandExecutor {
	
	private final Config config;
	private final Messages messages;
	private final ProfilesManager profilesManager;
	
	public CommandJoin(Config config, Messages messages, ProfilesManager profilesManager) {
		super(messages);
		
		this.config = config;
		this.messages = messages;
		this.profilesManager = profilesManager;
		
		String sendermsg = messages.get("error.only-for-players");
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		
		Validator senderval = new SenderIsPlayerValidator(sendermsg);
		Validator permval = new PermissionValidator("jobs.join", permmsg);
		Validator argsval = new ArgsCountValidator(1, argsmsg);
		Validator jobval = new JobValidator(config, messages);
		
		super.addValidators(senderval, permval, argsval, jobval);
	}
	
	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		String jobName = args.get(0);
		
		CommandExecutionData data = new JobExecutionData(sender, args, jobName);
		if(!validateExecution(data)) return;
		
		JobTypeEnum job = JobTypeEnum.valueOf(jobName.toUpperCase());
		JobConfiguration config = this.config.getJobConfig(job);
		
		String name = sender.getName();
		String jname = config.getName();
			
		// Checks for jobs limit
		PlayerProfile worker = profilesManager.getProfile(name);
		if(worker != null && worker.getJobsCount() + 1 >= this.config.getInt("max-jobs")) {
			messages.getAndSend(sender, "join.failed.max");
			return;
		} else if(worker == null) {
			worker = new PlayerProfile(name);
			profilesManager.createProfile(worker);
		}
		
		EmployeeProfile profile = profilesManager.getEmployeeProfile(name, job);
		if(profile != null) {
			messages.sendFormatted(sender, "join.failed.already", "%job%", jname);
			return;
		}
		
		profile = new EmployeeProfile(name, job);
		profilesManager.saveEmployeeProfile(profile);
		
		messages.sendFormatted(sender, "join.success", "%job%", jname);
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, CommandArguments args) {
		if(args.size() != 1 || !validateTabCompletion(sender, args)) return null;
		
		String arg = args.get(0).toLowerCase();
		
		List<String> matches = Arrays.stream(JobTypeEnum.values())
				.map(j -> j.toString().toLowerCase())
				.filter(j -> j.startsWith(arg))
				.collect(Collectors.toList());
		
		return matches;
	}
	
}
