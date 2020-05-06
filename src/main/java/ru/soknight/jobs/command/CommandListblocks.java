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
import ru.soknight.jobs.database.workspace.WorkspaceBlocksManager;
import ru.soknight.jobs.database.workspace.WorkspaceLinkedBlock;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.tool.CollectionsTool;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.SenderIsPlayerValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandListblocks extends ExtendedSubcommandExecutor {
	
	private final Config config;
	private final Messages messages;
	private final WorkspaceBlocksManager tilesManager;
	
	private final String header;
	private final String body;
	private final String footer;
	
	public CommandListblocks(Config config, Messages messages, WorkspaceBlocksManager tilesManager) {
		super(messages);
		
		this.config = config;
		this.messages = messages;
		this.tilesManager = tilesManager;
		
		this.header = messages.get("list.blocks.header");
		this.body = messages.get("list.blocks.body");
		this.footer = messages.get("list.blocks.footer");
		
		String sendermsg = messages.get("error.only-for-players");
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		
		Validator senderval = new SenderIsPlayerValidator(sendermsg);
		Validator permval = new PermissionValidator("jobs.list.blocks", permmsg);
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
		
		String jname = config.getName();
		
		int page = 1;
		if(args.size() > 1)
			try {
				page = Integer.parseInt(args.get(1));
			} catch (NumberFormatException e) {
				messages.sendFormatted(sender, "error.arg-is-not-int", "%arg%", args.get(1));
				return;
			}
		
		List<WorkspaceLinkedBlock> blocks = tilesManager.getLinkedBlocks(job);
		if(blocks == null || blocks.isEmpty()) {
			messages.sendFormatted(sender, "list.blocks.not-found", "%job%", jname);
			return;
		}
		
		int size = this.config.getInt("messages.list-size");
		List<WorkspaceLinkedBlock> onpage = CollectionsTool.getSubList(blocks, size, page);
		
		if(onpage.isEmpty()) {
			messages.sendFormatted(sender, "list.blocks.page-is-empty", "%page%", page);
			return;
		}
		
		int pages = blocks.size() / size;
		if(blocks.size() % size != 0) pages++;
		
		String header = messages.format(this.header, "%page%", page, "%total%", pages);
		
		List<String> body = onpage.stream()
				.map(b -> messages.format(this.body, "%x%", b.getX(), "%y%", b.getY(), "%z%", b.getZ()))
				.collect(Collectors.toList());
		
		messages.send(sender, header);
		body.forEach(m -> messages.send(sender, m));
		messages.send(sender, this.footer);
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
