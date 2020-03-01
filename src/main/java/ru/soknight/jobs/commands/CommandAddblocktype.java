package ru.soknight.jobs.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.WorkspaceBlocktype;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.utils.StringUtils;

public class CommandAddblocktype extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandAddblocktype(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "jobs.blocktypes.add", 3);
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
		
		Material material = Material.valueOf(args[2].toUpperCase());
		if(material == null) {
			sender.sendMessage(Messages.formatMessage("blocktype-unknown-material", "%material%", args[2]));
			return;
		}
		
		JobType job = JobType.valueOf(jobName.toUpperCase());
		
		String mstr = StringUtils.capitalizeFirst(material.name()).replace("_", " ");
		String jstr = Config.getJobConfig(job).getName();
		
		if(dbm.hasBlocktype(job, material)) {
			sender.sendMessage(Messages.formatMessage("blocktype-add-failed", "%material%", mstr, "%job%", jstr));
			return;
		}
		
		dbm.saveBlocktype(new WorkspaceBlocktype(job, material));
		sender.sendMessage(Messages.formatMessage("blocktype-added", "%material%", mstr, "%job%", jstr));
	}
	
}
