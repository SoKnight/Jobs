package ru.soknight.jobs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.WorkspaceBlock;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.utils.ListUtils;
import ru.soknight.jobs.utils.StringUtils;

public class CommandListblocks extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandListblocks(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "jobs.list.blocks", 2);
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
		
		List<WorkspaceBlock> blocks = dbm.getBlocks(job);
		if(blocks == null || blocks.isEmpty()) {
			sender.sendMessage(Messages.formatMessage("listblocks-not-found", "%job%", jname));
			return;
		}
		
		int size = Config.getConfig().getInt("messages.list-size");
		List<WorkspaceBlock> onpage = ListUtils.getBlocksOnPage(blocks, size, page);
		
		if(onpage.isEmpty()) {
			sender.sendMessage(Messages.formatMessage("listblocks-page-is-empty", "%page%", page));
			return;
		}
		
		int pages = blocks.size() / size;
		if(blocks.size() % size != 0) pages++;
		
		String header = Messages.formatRawMessage("listblocks-header", "%page%", page, "%max_page%", pages);
		String body = Messages.getRawMessage("listblocks-body");
		String footer = Messages.getRawMessage("listblocks-footer");
		
		List<String> output = new ArrayList<>();
		onpage.forEach(b -> output.add(StringUtils.format(body, "%x%", b.getX(), "%y%", b.getY(), "%z%", b.getZ())));
		
		sender.sendMessage(header);
		output.forEach(s -> sender.sendMessage(s));
		sender.sendMessage(footer);
	}
	
}
