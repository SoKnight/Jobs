package ru.soknight.jobs.handlers;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.soknight.jobs.enums.JobType;

public class SessionManager {

	private Map<String, Session> sessions = new HashMap<>();
	
	public boolean doneSession(String player) {
		if(!hasSession(player)) return false;
		
		sessions.remove(player);
		return true;
	}
	
	public Session getSession(String player) {
		return sessions.get(player);
	}
	
	public boolean hasSession(String player) {
		return sessions.containsKey(player);
	}
	
	public boolean startSession(String player, JobType job) {
		if(hasSession(player)) return false;
		
		Session session = new Session(player, job);
		sessions.put(player, session);
		return true;
	}
	
	public void restart() {
		sessions.clear();
	}
	
	@Getter
	@AllArgsConstructor
	public class Session {
		private final String name;
		private final JobType job;
	}
	
}
