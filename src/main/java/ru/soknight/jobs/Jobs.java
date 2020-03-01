package ru.soknight.jobs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import ru.soknight.jobs.commands.CommandsHandler;
import ru.soknight.jobs.commands.CommandsTabCompleter;
import ru.soknight.jobs.database.Database;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.InfoSender;
import ru.soknight.jobs.handlers.JobTasksHandler;
import ru.soknight.jobs.handlers.SessionManager;
import ru.soknight.jobs.listeners.JobProcessListener;
import ru.soknight.jobs.listeners.JobStatusWatchdog;
import ru.soknight.jobs.listeners.SessionListener;
import ru.soknight.jobs.utils.Logger;

public class Jobs extends JavaPlugin {

	@Getter private static Jobs instance;
	@Getter private DatabaseManager databaseManager;
	@Getter private SessionManager sessionManager;
	
	@Override
	public void onEnable() {
		instance = this;
		
		// Refreshing
		Config.refresh();
		Messages.refresh();
		InfoSender.refresh();
		
		// Start tasks
		JobTasksHandler.start();
		
		// Loading database and session manager
		try {
			Database database = new Database();
			databaseManager = new DatabaseManager(database);
			sessionManager = new SessionManager();
		} catch (Exception e) {
			Logger.error("Failed connect to database: " + e.getLocalizedMessage());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		// Try hook into PAPI
		hookIntoPapi();
		
		// Register listeners and executors
		getCommand("jobs").setExecutor(new CommandsHandler(sessionManager, databaseManager));
		getCommand("jobs").setTabCompleter(new CommandsTabCompleter(databaseManager));
		
		PluginManager man = getServer().getPluginManager();
		man.registerEvents(new JobProcessListener(databaseManager), this);
		man.registerEvents(new JobStatusWatchdog(databaseManager), this);
		man.registerEvents(new SessionListener(sessionManager, databaseManager), this);
		
		Logger.info("Enabled!");
	}
	
	private void hookIntoPapi() {
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            boolean hooked = new JobsExpansion(this, databaseManager).register();
            if(hooked) Logger.info("Hooked into PlaceholdersAPI!");
            else Logger.warning("Hooking to PlaceholdersAPI failed.");
		} else Logger.info("PlaceholdersAPI not found, hooking cancelled.");
	}
	
	@Override
	public void onDisable() {
		JobTasksHandler.stop();
		if(databaseManager != null) databaseManager.shutdown();
		
		Logger.info("Disabled!");
	}
	
}
