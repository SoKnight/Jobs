package ru.soknight.jobs.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import ru.soknight.jobs.command.validation.JobExecutionData;
import ru.soknight.jobs.command.validation.JobValidator;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.profile.EmployeeProfile;
import ru.soknight.jobs.database.profile.ProfilesManager;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.format.FloatFormatter;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandInfo extends ExtendedSubcommandExecutor {
	
	private final Config config;
	private final Messages messages;
	
	private final ProfilesManager profilesManager;
	private final FloatFormatter formatter;
	
	private final String header;
	private final String footer;
	private final ConfigurationSection section;
	
	public CommandInfo(Config config, Messages messages, ProfilesManager profilesManager) {
		super(messages);
		
		this.config = config;
		this.messages = messages;
		
		this.profilesManager = profilesManager;
		this.formatter = new FloatFormatter('.');
		
		this.header = messages.get("info.header");
		this.section = messages.getFileConfig().getConfigurationSection("info.list");
		this.footer = messages.get("info.footer");
		
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		
		Validator permval = new PermissionValidator("jobs.info", permmsg);
		Validator argsval = new ArgsCountValidator(1, argsmsg);
		Validator jobval = new JobValidator(config, messages);
		
		super.addValidators(permval, argsval, jobval);
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
		
		boolean other = args.size() > 1;
		if(other)
			name = args.get(1);
		else
			if(!(sender instanceof Player)) {
				messages.getAndSend(sender, "error.only-for-players");
				return;
			}
		
		EmployeeProfile profile = profilesManager.getEmployeeProfile(name, job);
		if(profile == null) {
			if(other)
				messages.sendFormatted(sender, "info.failed-other", "%player%", name, "%job%", jname);
			else messages.getAndSend(sender, "error.you-dont-work-here");
			return;
		}
		
		messages.send(sender, header);
		
		if(section.contains("job"))
			messages.sendFormatted(sender, "info.list.job",
					"%job%", jname);
		if(section.contains("employee"))
			messages.sendFormatted(sender, "info.list.employee",
					"%employee%", name);
		if(section.contains("employees"))
			messages.sendFormatted(sender, "info.list.employees",
					"%employees%", profilesManager.getEmployeesCount(job));
		if(section.contains("level"))
			messages.sendFormatted(sender, "info.list.level",
					"%level%", profile.getLevel(),
					"%exp%", profile.getProgress(),
					"%needed%", profile.getNeeded(config));
		if(section.contains("bonus")) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(name);
			String boost = player != null && player.isOnline()
					? formatter.shortToString(config.getBoost(player.getPlayer()), 1)
					: this.config.getColoredString("messages.unknown-boost");
			
			messages.sendFormatted(sender, "info.list.bonus", "%bonus%", boost);
		}
			
		messages.send(sender, footer);
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, CommandArguments args) {
		if(args.isEmpty() || args.size() > 2 || !validateTabCompletion(sender, args)) return null;
		
		if(args.size() == 1) {
			String arg = args.get(0).toLowerCase();
			
			List<String> matches = Arrays.stream(JobTypeEnum.values())
					.map(j -> j.toString().toLowerCase())
					.filter(j -> j.startsWith(arg))
					.collect(Collectors.toList());
			
			return matches;
		} else {
			// Adds other offline players if interaction with them is permitted
			if(sender.hasPermission("jobs.info.other")) {
				String arg = args.get(1).toLowerCase();
				
				List<String> matches = Arrays.asList(Bukkit.getOfflinePlayers()).parallelStream()
						.map(OfflinePlayer::getName)
						.filter(n -> n.toLowerCase().startsWith(arg))
						.collect(Collectors.toList());
				
				return matches;
			}
		}
		
		return null;
	}
	
}
