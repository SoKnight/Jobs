package ru.soknight.jobs.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.WorkerProfile;
import ru.soknight.jobs.enums.JobType;

public class TabCompletionHelper {

	private static final List<String> subcommands = Arrays.asList("help", "join", "leave", "list", "listtypes",
			"info", "listblocks", "reload", "start", "done", "edit", "finish", "addblocktype", "remblocktype");
	
	private DatabaseManager dbm;
	private List<String> output;
	
	public TabCompletionHelper(DatabaseManager dbm) {
		this.dbm = dbm;
		this.output = new ArrayList<>();
	}
	
	public List<String> getCompletions() {
		return output;
	}

	public void putSubcommands(String start) {
		for(String s : subcommands)
			if(s.startsWith(start))
				output.add(s);
	}

	public void putAllJobs(String start) {
		for(JobType job : JobType.values()) {
			String joblower = job.name().toLowerCase();
			if(joblower.startsWith(start))
				output.add(joblower);
		}
	}

	public void putAvailableJobs(String start, String name) {
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null) putAllJobs(start);
		else
			for(JobType job : JobType.values()) {
				if(dbm.hasJobProfile(name, job)) continue;
				String joblower = job.name().toLowerCase();
				if(joblower.startsWith(start))
					output.add(joblower);
			}
	}

	public void putJoinedJobs(String start, String name) {
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null) return;
		else
			for(JobType job : JobType.values()) {
				if(!dbm.hasJobProfile(name, job)) continue;
				String joblower = job.name().toLowerCase();
				if(joblower.startsWith(start))
					output.add(joblower);
			}
	}
	
}
