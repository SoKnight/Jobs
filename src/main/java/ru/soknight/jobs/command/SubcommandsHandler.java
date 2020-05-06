package ru.soknight.jobs.command;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.database.SessionManager;
import ru.soknight.jobs.database.profile.ProfilesManager;
import ru.soknight.jobs.database.workspace.WorkspaceBlocksManager;
import ru.soknight.jobs.listener.EmployeesWatchdog;
import ru.soknight.lib.command.AbstractSubcommandsHandler;
import ru.soknight.lib.configuration.Messages;

public class SubcommandsHandler extends AbstractSubcommandsHandler {
	
	public SubcommandsHandler(Jobs plugin, Config config, Messages messages, EmployeesWatchdog watchdog,
			ProfilesManager profilesManager, WorkspaceBlocksManager tilesManager, SessionManager sessionManager) {
		
		super(messages);
		
		super.setExecutor("help", new CommandHelp(messages));
		super.setExecutor("join", new CommandJoin(config, messages, profilesManager));
		super.setExecutor("leave", new CommandLeave(config, messages, profilesManager));
		super.setExecutor("info", new CommandInfo(config, messages, profilesManager));
		super.setExecutor("list", new CommandList(config, messages, profilesManager));
		super.setExecutor("start", new CommandStart(config, messages, profilesManager, watchdog));
		super.setExecutor("done", new CommandDone(messages, profilesManager, watchdog));
		super.setExecutor("listblocks", new CommandListblocks(config, messages, tilesManager));
		super.setExecutor("selection", new CommandSelection(config, messages, sessionManager));
		super.setExecutor("reload", new CommandReload(plugin, messages));
	}

}
