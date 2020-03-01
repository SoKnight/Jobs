package ru.soknight.jobs.commands;

public interface ITabCompleter {

	void putSubcommands(String start);
	
	void putAllJobs(String start);
	
	void putAvailableJobs(String start, String name);
	
	void putJoinedJobs(String start, String name);
	
}
