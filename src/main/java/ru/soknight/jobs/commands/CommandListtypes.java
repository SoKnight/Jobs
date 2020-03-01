package ru.soknight.jobs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.utils.ListUtils;
import ru.soknight.jobs.utils.StringUtils;

public class CommandListtypes extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandListtypes(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "jobs.list.types", 2);
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
		
		JobType job = JobType.valueOf(jobName.toUpperCase());
		String jname = Config.getJobConfig(job).getName();
		
		int page = 1;
		if(args.length > 2)
			if(!argIsInteger(args[2])) return;
			else page = Integer.parseInt(args[2]);
		
		List<Material> blocktypes = dbm.getBlocktypes(job);
		if(blocktypes == null || blocktypes.isEmpty()) {
			sender.sendMessage(Messages.formatMessage("listtypes-not-found", "%job%", jname));
			return;
		}
		
		int size = Config.getConfig().getInt("messages.list-size");
		List<Material> onpage = ListUtils.getBlocktypesOnPage(blocktypes, size, page);
		
		if(onpage.isEmpty()) {
			sender.sendMessage(Messages.formatMessage("listtypes-page-is-empty", "%page%", page));
			return;
		}
		
		int pages = blocktypes.size() / size;
		if(blocktypes.size() % size != 0) pages++;
		
		String header = Messages.formatRawMessage("listtypes-header", "%page%", page, "%max_page%", pages);
		String body = Messages.getRawMessage("listtypes-body");
		String footer = Messages.getRawMessage("listtypes-footer");
		
		List<String> output = new ArrayList<>();
		onpage.forEach(t -> {
			String material = StringUtils.capitalizeFirst(t.name()).replace("_", " ");
			output.add(StringUtils.format(body, "%material%", material));
		});
		
		sender.sendMessage(header);
		output.forEach(s -> sender.sendMessage(s));
		sender.sendMessage(footer);
	}
	
}
