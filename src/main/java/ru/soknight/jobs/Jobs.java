package ru.soknight.jobs;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import ru.soknight.jobs.command.SubcommandsHandler;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.MessagesProvider;
import ru.soknight.jobs.database.Database;
import ru.soknight.jobs.database.SessionManager;
import ru.soknight.jobs.database.profile.ProfilesManager;
import ru.soknight.jobs.database.workspace.WorkspaceBlocksManager;
import ru.soknight.jobs.listener.EmployeesWatchdog;
import ru.soknight.jobs.listener.SessionListener;
import ru.soknight.jobs.listener.WorkingListener;
import ru.soknight.lib.configuration.Messages;

public class Jobs extends JavaPlugin {

	protected ProfilesManager profilesManager;
	protected WorkspaceBlocksManager workspaceTilesManager;
	
	protected SessionManager sessionManager;
	
	protected EmployeesWatchdog employeesWatchdog;
	protected WorkingListener workingListener;
	
	protected Config config;
	protected MessagesProvider messagesProvider;
	protected Messages messages;
	
	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();
		
		// Configs initialization
		refreshConfigs();
		
		// Database initialization
		try {
			Database database = new Database(this, config);
			this.profilesManager = new ProfilesManager(this, database);
			this.workspaceTilesManager = new WorkspaceBlocksManager(this, database);
		} catch (Exception e) {
			getLogger().severe("Failed to initialize database: " + e.getLocalizedMessage());
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		// Event listeners initialization
		registerListeners();
		
		// Commands executors initialization
		registerCommands();
		
		// Trying to hook into PAPI and Vault
		hookInto();
		
		long time = System.currentTimeMillis() - start;
		getLogger().info("Bootstrapped in " + time + " ms.");
	}
	
	@Override
	public void onDisable() {
		// Kicks all working players
		if(employeesWatchdog != null) {
			Set<String> keys = employeesWatchdog.getEmployees().keySet();
			if(!keys.isEmpty())
				keys.stream()
					.map(n -> getServer().getOfflinePlayer(n))
					.filter(p -> p != null)
					.collect(Collectors.toSet())
					.forEach(p -> employeesWatchdog.onWorkingFinished(p));
		}
		
		// Shutdowns the blocks regenerator task
		if(workingListener != null)
			workingListener.shutdownRegenerator();
		
		// Shutdowns the database managers
		if(profilesManager != null)
			profilesManager.shutdown();
		
		if(workspaceTilesManager != null)
			workspaceTilesManager.shutdown();
	}
	
	public void refreshConfigs() {
		this.config = new Config(this);
		this.messagesProvider = new MessagesProvider(this, config);
		this.messages = messagesProvider.getMessages();
	}
	
	private void registerListeners() {
		this.sessionManager = new SessionManager();
		
		this.employeesWatchdog = new EmployeesWatchdog(config, messages, profilesManager);
		employeesWatchdog.register(this);
		
		this.workingListener = new WorkingListener(this, config, messages, profilesManager, workspaceTilesManager);
		workingListener.register(this);
		
		new SessionListener(this, config, messages, sessionManager, workspaceTilesManager).register(this);
	}
	
	private void registerCommands() {
		SubcommandsHandler jobsHandler = new SubcommandsHandler(
				this, config, messages,
				employeesWatchdog,
				profilesManager,
				workspaceTilesManager,
				sessionManager);
		
		PluginCommand jobs = getCommand("jobs");
		
		jobs.setExecutor(jobsHandler);
		jobs.setTabCompleter(jobsHandler);
	}
	
	private void hookInto() {
		if(config.getBoolean("hooks.papi")) {
			if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
				JobsExpansion jobsExpansion = new JobsExpansion(this, config, profilesManager);
				
				if(jobsExpansion.register())
					getLogger().info("Hooked into PlaceholdersAPI successfully.");
				else getLogger().warning("Hooking into PlaceholdersAPI failed.");
				
			} else getLogger().info("Couldn't find PlaceholdersAPI to hook into, ignoring it.");
		}
	}
	
	public void refresh() {
		config.refresh();
		config.loadJobsConfiguration(this);
		messagesProvider.update(config);
		
		registerCommands();
	}
	
}
