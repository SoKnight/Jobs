package ru.soknight.jobs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.soknight.jobs.commands.SubCommands;
import ru.soknight.jobs.database.Database;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.JobProcessHandler;
import ru.soknight.jobs.handlers.JobStatusWatchdog;
import ru.soknight.jobs.handlers.JobTasksHandler;
import ru.soknight.jobs.handlers.SelectionToolsHandler;
import ru.soknight.jobs.utils.InfoSender;
import ru.soknight.jobs.utils.Logger;
import ru.soknight.jobs.utils.WorkStatusSwitcher;

public class Jobs extends JavaPlugin {

	private static Jobs instance;
	private Database database;
	
	@Override
	public void onEnable() {
		instance = this;
		
		// Refreshing
		Config.refresh();
		Messages.refresh();
		InfoSender.refresh();
		SelectionToolsHandler.refreshDefaults();
		
		// Start tasks
		JobTasksHandler.start();
		
		// Loading database
		try {
			database = new Database();
			DatabaseManager.loadFromDatabase();
		} catch (Exception e) {
			Logger.error("Couldn't connect database type " + Config.config.getString("database.type") + ":");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		// Try hook into PAPI
		hookIntoPapi();
		
		// Register listeners and executors
		getCommand("jobs").setExecutor(new SubCommands());
		getCommand("jobs").setTabCompleter(new SubCommands());
		
		PluginManager man = getServer().getPluginManager();
		man.registerEvents(new JobProcessHandler(), this);
		man.registerEvents(new JobStatusWatchdog(), this);
		man.registerEvents(new SelectionToolsHandler(), this);
		
		Logger.info("Enabled!");
	}
	
	private void hookIntoPapi() {
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            boolean hooked = new JobsExpansion(this).register();
            if(hooked) Logger.info("Hooked into PlaceholdersAPI.");
            else Logger.warning("Hooking to PlaceholdersAPI failed.");
		} else Logger.info("PlaceholdersAPI not found, hooking cancelled.");
	}
	
	@Override
	public void onDisable() {
		DatabaseManager.saveToDatabase();
		WorkStatusSwitcher.doneAll();
		JobTasksHandler.stop();
		
		Logger.info("Disabled!");
	}

	public static Jobs getInstance() {
		return instance;
	}

	public Database getDatabase() {
		return database;
	}
	
}
