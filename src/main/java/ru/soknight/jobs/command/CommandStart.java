package ru.soknight.jobs.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.command.validation.JobExecutionData;
import ru.soknight.jobs.command.validation.JobValidator;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.profile.PlayerProfile;
import ru.soknight.jobs.database.profile.ProfilesManager;
import ru.soknight.jobs.listener.EmployeesWatchdog;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.SenderIsPlayerValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandStart extends ExtendedSubcommandExecutor {
	
	private final Config config;
	private final Messages messages;
	
	private final ProfilesManager profilesManager;
	private final EmployeesWatchdog watchdog;
	
	public CommandStart(Config config, Messages messages, ProfilesManager profilesManager,
			EmployeesWatchdog watchdog) {
		
		super(messages);
		
		this.config = config;
		this.messages = messages;
		
		this.profilesManager = profilesManager;
		this.watchdog = watchdog;
		
		String sendermsg = messages.get("error.only-for-players");
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		
		Validator senderval = new SenderIsPlayerValidator(sendermsg);
		Validator permval = new PermissionValidator("jobs.work", permmsg);
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
		
		Player player = (Player) sender;
		String name = player.getName();
		
		PlayerProfile profile = profilesManager.getProfile(name);
		if(profile == null || !profilesManager.hasEmployeeProfile(name, job)) {
			messages.getAndSend(sender, "error.you-dont-work-here");
			return;
		}
		
		if(profile != null && profile.getCurrentJob() != null) {
			messages.getAndSend(sender, "working.start.failed");
			return;
		}
		
		profile.setCurrentJob(job);
		profilesManager.updateProfile(profile);
		
		watchdog.onWorkingStarted(player, job);
		
		JobConfiguration config = this.config.getJobConfig(job);
		String jname = config.getName();
		
		messages.sendFormatted(sender, "working.start.success", "%job%", jname);
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, CommandArguments args) {
		if(args.size() != 1 || !validateTabCompletion(sender, args)) return null;
		
		String arg = args.get(0).toLowerCase();
		
		List<String> matches = Arrays.asList(JobTypeEnum.values()).stream()
				.map(j -> j.toString().toLowerCase())
				.filter(j -> j.startsWith(arg))
				.collect(Collectors.toList());
		
		return matches;
	}
	
}
