package ru.soknight.jobs.database;

import java.util.HashMap;
import java.util.Map;

import ru.soknight.jobs.configuration.JobTypeEnum;

public class SessionManager {

	private final Map<String, JobTypeEnum> sessions = new HashMap<>();
	
	public boolean doneSession(String player) {
		if(!hasSession(player)) return false;
		
		sessions.remove(player);
		return true;
	}
	
	public JobTypeEnum getSessionJob(String player) {
		return sessions.get(player);
	}
	
	public boolean hasSession(String player) {
		return sessions.containsKey(player);
	}
	
	public boolean startSession(String player, JobTypeEnum job) {
		if(hasSession(player)) return false;
		
		sessions.put(player, job);
		return true;
	}
	
	public void clear() {
		sessions.clear();
	}
	
}
