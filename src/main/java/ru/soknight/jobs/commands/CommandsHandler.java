package ru.soknight.jobs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import lombok.RequiredArgsConstructor;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.SessionManager;

@RequiredArgsConstructor
public class CommandsHandler implements CommandExecutor {

	private final SessionManager sessionManager;
	private final DatabaseManager databaseManager;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Messages.getMessage("error-no-args"));
			return true;
		}
		
		try {
			switch(args[0]) {
			case "help":
				new CommandHelp(sender).execute();
				break;
			case "join":
				new CommandJoin(sender, args, databaseManager).execute();
				break;
			case "leave":
				new CommandLeave(sender, args, databaseManager).execute();
				break;
			case "info":
				new CommandInfo(sender, args, databaseManager).execute();
				break;
			case "list":
				new CommandList(sender, databaseManager).execute();
				break;
			case "listtypes":
				new CommandListtypes(sender, args, databaseManager).execute();
				break;
			case "listblocks":
				new CommandListblocks(sender, args, databaseManager).execute();
				break;
			case "reload":
				new CommandReload(sender).execute();
				break;
			case "start":
				new CommandStart(sender, args, databaseManager).execute();
				break;
			case "done":
				new CommandDone(sender, databaseManager).execute();
				break;
			case "edit":
				new CommandEdit(sender, args, sessionManager).execute();
				break;
			case "finish":
				new CommandFinish(sender, sessionManager).execute();
				break;
			case "addblocktype":
				new CommandAddblocktype(sender, args, databaseManager).execute();
				break;
			case "remblocktype":
				new CommandRemblocktype(sender, args, databaseManager).execute();
				break;
			default:
				sender.sendMessage(Messages.getMessage("error-command-not-found"));
				break;
			}
		} catch (NotLoadedConfigException e) {
			sender.sendMessage(Messages.formatMessage("error-is-not-init", "%file%", e.getJobType() + ".yml"));
		}
		return true;
	}

}
