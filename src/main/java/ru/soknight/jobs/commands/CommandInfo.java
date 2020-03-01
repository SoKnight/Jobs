package ru.soknight.jobs.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobProfile;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.objects.JobConfig;
import ru.soknight.jobs.utils.ExpUtils;
import ru.soknight.jobs.utils.StringUtils;

public class CommandInfo extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandInfo(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "jobs.info", 2);
		this.sender = sender;
		this.args = args;
		this.dbm = dbm;
	}
	
	@Override
	public void execute() throws NotLoadedConfigException {
		if(!hasPermission()) return;
		if(!isCorrectUsage()) return;
		
		String jobName = args[1];
		if(!isJobExist(jobName)) return;
		
		String name = sender.getName();
		if(args.length > 2) {
			name = args[2];
			if(!hasPermission("jobs.info.other")) return;
			if(!isPlayerExist(name)) return;
		} else if(!isPlayerRequired()) return;
		
		JobType job = JobType.valueOf(args[1].toUpperCase());
		JobConfig info = Config.getJobConfig(job);
		String jname = info.getName();
			
		JobProfile profile = dbm.getJobProfile(name, job);
		if(profile == null) {
			sender.sendMessage(Messages.getMessage("error-dont-work"));
			return;
		}
		
		int level = profile.getLevel(), exp = profile.getProgress();
		int needed = level == info.getMaxLevel() ? ExpUtils.getNeededExp(job, level) : ExpUtils.getNeededExp(job, level + 1);
		
		String boost = Config.getConfig().getString("messages.unknown-boost").replace("&", "\u00a7");
		OfflinePlayer offpl = Bukkit.getOfflinePlayer(name);
		if(offpl.isOnline()) boost = String.valueOf(info.getBoost(offpl.getPlayer()));
		
		String lvlstr = String.valueOf(level), expstr = String.valueOf(exp), nddstr = String.valueOf(needed);
		String wcount = String.valueOf(dbm.getWorkersCount(job));
		
		if(level == info.getMaxLevel()) {
			String prefix = Config.getConfig().getString("messages.max-level-prefix").replace("&", "\u00a7");
			lvlstr = prefix + lvlstr;
		}
		
		List<String> body = Messages.getStringList("info-body");
		
		sender.sendMessage(Messages.getRawMessage("info-header"));
		
		for(String s : body)
			sender.sendMessage(StringUtils.format(s, "%name%", jname, "%worker%", name, "%workers%", wcount,
					"%level%", lvlstr, "%exp%", expstr, "%needed%", nddstr, "%bonus%", boost));
		
		sender.sendMessage(Messages.getRawMessage("info-footer"));
	}
	
}
