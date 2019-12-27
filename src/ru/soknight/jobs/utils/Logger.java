package ru.soknight.jobs.utils;

import ru.soknight.jobs.Jobs;

public class Logger {
	
	public static void info(String info) {
		Jobs.getInstance().getLogger().info(info);
	}
	
	public static void warning(String warning) {
		Jobs.getInstance().getLogger().warning(warning);
	}
	
	public static void error(String error) {
		Jobs.getInstance().getLogger().severe(error);
	}
	
}
