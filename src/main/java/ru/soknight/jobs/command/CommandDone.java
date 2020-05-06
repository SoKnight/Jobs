package ru.soknight.jobs.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.database.profile.PlayerProfile;
import ru.soknight.jobs.database.profile.ProfilesManager;
import ru.soknight.jobs.listener.EmployeesWatchdog;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandDone extends ExtendedSubcommandExecutor {
	
	private final Messages messages;
	
	private final ProfilesManager profilesManager;
	private final EmployeesWatchdog watchdog;
	
	public CommandDone(Messages messages, ProfilesManager profilesManager, EmployeesWatchdog watchdog) {
		
		super(messages);
		
		this.messages = messages;
		
		this.profilesManager = profilesManager;
		this.watchdog = watchdog;
		
		String permmsg = messages.get("error.no-permissions");
		
		Validator permval = new PermissionValidator("jobs.work", permmsg);
		
		super.addValidators(permval);
	}
	
	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		if(!validateExecution(sender, args)) return;
		
		Player player = (Player) sender;
		String name = player.getName();
		
		PlayerProfile profile = profilesManager.getProfile(name);
		if(profile == null || profile.getCurrentJob() == null) {
			messages.getAndSend(sender, "working.finish.failed");
			return;
		}
		
		watchdog.onWorkingFinished(player);
		
		profile.setCurrentJob(null);
		profilesManager.updateProfile(profile);
	}
	
}
