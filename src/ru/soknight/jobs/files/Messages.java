package ru.soknight.jobs.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.units.HelpCommand;
import ru.soknight.jobs.utils.Logger;

public class Messages {

	public static FileConfiguration config;
	private static Map<String, HelpCommand> help_list;
	private static Set<String> keys = new HashSet<>();
	
	public static void refresh() {
		Jobs instance = Jobs.getInstance();
		File datafolder = instance.getDataFolder();
		if(!datafolder.isDirectory()) datafolder.mkdirs();
		File file = new File(instance.getDataFolder(), "messages_en.yml");
		if(!file.exists()) {
			try {
				Files.copy(instance.getResource("messages_en.yml"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Logger.info("Generated new messages file.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
		keys = config.getKeys(true);
		initHelpList();
	}
	
	public static String getMessage(String section) {
		if(!keys.contains(section)) {
			Logger.error("Couldn't load message from messages_en.yml: " + section);
			return "Whoops! Message not found :(";
		}
		String output = config.getString(section).replace("&", "\u00A7");
		if(Config.use_prefix) output = Config.prefix + output;
		return output;
	}
	
	public static String getRawMessage(String section) {
		if(!keys.contains(section)) {
			Logger.error("Couldn't load message from messages_en.yml: " + section);
			return "Whoops! Message not found :(";
		}
		String output = config.getString(section).replace("&", "\u00A7");
		return output;
	}
	
	public static HelpCommand getHelpString(String key) {
		return help_list.get(key);
	}
	
	public static List<String> getStringList(String key) {
		List<String> output = new ArrayList<>();
		for(String s : config.getStringList(key))
			output.add(s.replace("&", "\u00A7"));
		return output;
	}
	
	private static void initHelpList() {
		String format = getMessage("help-body");
		help_list = new HashMap<>();
		
		HelpCommand 
			addtool = new HelpCommand(format, "addtool", getDescription("addtool"), "job"),
			done = new HelpCommand(format, "done", getDescription("done")),
			help = new HelpCommand(format, "help", getDescription("help")),
			join = new HelpCommand(format, "join", getDescription("join"), "job"),
			join_other = new HelpCommand(format, "join", getDescription("join-other"), "job", "target"),
			leave = new HelpCommand(format, "leave", getDescription("leave"), "job"),
			leave_other = new HelpCommand(format, "leave", getDescription("leave-other"), "job", "target"),
			info = new HelpCommand(format, "info", getDescription("info"), "job"),
			info_other = new HelpCommand(format, "info", getDescription("info-other"), "job", "target"),
			list = new HelpCommand(format, "list", getDescription("list")),
			reload = new HelpCommand(format, "reload", getDescription("reload")),
			remtool = new HelpCommand(format, "remtool", getDescription("remtool"), "job"),
			start = new HelpCommand(format, "reload", getDescription("reload"), "job");
		
		help_list.put("addtool", addtool);
		help_list.put("done", done);
		help_list.put("help", help);
		help_list.put("join", join);
		help_list.put("join-other", join_other);
		help_list.put("leave", leave);
		help_list.put("leave-other", leave_other);
		help_list.put("info", info);
		help_list.put("info-other", info_other);
		help_list.put("list", list);
		help_list.put("reload", reload);
		help_list.put("remtool", remtool);
		help_list.put("start", start);
	}
	
	private static String getDescription(String command) {
		return config.getString("help-descriptions." + command).replace("&", "\u00A7");
	}
	
}
