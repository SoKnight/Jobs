package ru.soknight.jobs.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.utils.Logger;

public class DatabaseManager {
	
	private static Map<String, WorkerProfile> workers = new HashMap<>();
	private static Map<JobType, JobInfo> jobinfo = new HashMap<>();
	
    public static void loadFromDatabase() {
    	Database db = Jobs.getInstance().getDatabase();
		String query = "SELECT player, job, level, progress FROM workers";
		
		JobInfo miners = new JobInfo(JobType.MINER);
		JobInfo woodcutters = new JobInfo(JobType.WOODCUTTER);
		JobInfo hunters = new JobInfo(JobType.HUNTER);
		JobInfo fishermans = new JobInfo(JobType.FISHERMAN);
		JobInfo farmers = new JobInfo(JobType.FARMER);
		
		jobinfo.put(JobType.MINER, miners);
		jobinfo.put(JobType.WOODCUTTER, woodcutters);
		jobinfo.put(JobType.HUNTER, hunters);
		jobinfo.put(JobType.FISHERMAN, fishermans);
		jobinfo.put(JobType.FARMER, farmers);
		
		try {
			Connection connection = db.getConnection();
			Statement statement = connection.createStatement();
			
			ResultSet output = statement.executeQuery(query);
			long start = System.currentTimeMillis();
			while(output.next()) {
				String name = output.getString("player");
				JobType job = JobType.valueOf(output.getString("job"));
				int level = output.getInt("level");
				int progress = output.getInt("progress");
				JobProfile profile = new JobProfile(name, job, level, progress);
				// Refresh job info
				JobInfo info = jobinfo.get(job);
				info.addWorker(name);
				jobinfo.put(job, info);
				// Setup job profile for user
				WorkerProfile worker = getProfile(name);
				worker.setJobProfile(job, profile);
				workers.put(name, worker);
			}
			long current = System.currentTimeMillis();
			Logger.info("Loaded " + workers.size() + " workers. Time took: " + (current - start) + " ms.");
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static void saveToDatabase() {
		Database db = Jobs.getInstance().getDatabase();
		String insert = "INSERT INTO workers (player, job, level, progress) VALUES (?, ?, ?, ?);";
		String clean = "DELETE FROM workers;";
		
		try {
			Connection connection = db.getConnection();
			connection.prepareStatement(clean).executeUpdate();
			
			if(!workers.isEmpty()) {
				PreparedStatement stm = connection.prepareStatement(insert);
				for(String name : workers.keySet()) {
					WorkerProfile worker = workers.get(name);
					for(JobType type : worker.getJobs()) {
						JobProfile profile = worker.getJobProfile(type);
						stm.setString(1, name);
						stm.setString(2, type.name());
						stm.setInt(3, profile.getLevel());
						stm.setInt(4, profile.getProgress());
						stm.executeUpdate();
						stm.clearParameters();
					}
				}
				stm.close();
			}
			
			
			Logger.info(workers.size() + " workers saved to database.");
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    //
    // Blocks
    //
    
    public static void addBlock(Location loc, JobType job) {
		Database db = Jobs.getInstance().getDatabase();
		String insert = "INSERT INTO blocks (world, x, y, z, job) VALUES (?, ?, ?, ?, ?);";
		
		try {
			Connection connection = db.getConnection();
			
			PreparedStatement stm = connection.prepareStatement(insert);
			stm.setString(1, loc.getWorld().getName());
			stm.setInt(2, loc.getBlockX());
			stm.setInt(3, loc.getBlockY());
			stm.setInt(4, loc.getBlockZ());
			stm.setString(5, job.name());
			stm.executeUpdate();

			stm.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    public static boolean hasBlock(Location loc, JobType job) {
    	Database db = Jobs.getInstance().getDatabase();
		String search = "SELECT * FROM blocks WHERE world=? AND x=? AND y=? AND z=? AND job=?;";
		
		try {
			Connection connection = db.getConnection();
			
			PreparedStatement stm = connection.prepareStatement(search);
			stm.setString(1, loc.getWorld().getName());
			stm.setInt(2, loc.getBlockX());
			stm.setInt(3, loc.getBlockY());
			stm.setInt(4, loc.getBlockZ());
			stm.setString(5, job.name());
			
			ResultSet output = stm.executeQuery();
			
			int count = 0;
			while(output.next()) count++;
			
			stm.close();
			connection.close();
			
			if(count == 0) return false;
			else return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    public static void removeBlock(Location loc, JobType job) {
		Database db = Jobs.getInstance().getDatabase();
		String delete = "DELETE FROM blocks WHERE world=? AND x=? AND y=? AND z=? AND job=?;";
		
		try {
			Connection connection = db.getConnection();
			
			PreparedStatement stm = connection.prepareStatement(delete);
			stm.setString(1, loc.getWorld().getName());
			stm.setInt(2, loc.getBlockX());
			stm.setInt(3, loc.getBlockY());
			stm.setInt(4, loc.getBlockZ());
			stm.setString(5, job.name());
			stm.executeUpdate();
			
			stm.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    //
    // General methods
    //
    
    public static boolean join(String name, JobProfile profile) {
    	WorkerProfile worker = new WorkerProfile(name);
    	if(isWorker(name)) worker = getProfile(name);
    	
    	JobType job = profile.getJob();
    	if(worker.hasJobProfile(job)) return false;
    	
    	worker.setJobProfile(job, profile);
    	setProfile(name, worker);
    	
    	getJobInfo(job).addWorker(name);
    	return true;
    }
    
    public static boolean leave(String name, JobType job) {
    	if(!isWorker(name)) return false;
    	WorkerProfile worker = getProfile(name);
    	
    	if(!worker.hasJobProfile(job)) return false;
    	worker.removeJobProfile(job);
    	setProfile(name, worker);
    	
    	getJobInfo(job).removeWorker(name);
    	return true;
    }
    
    
    
    //
    // Worker profiles
    //
    
    public static boolean isWorker(String name) {
    	return workers.containsKey(name);
    }
    
    public static WorkerProfile getProfile(String name) {
    	if(!isWorker(name)) return new WorkerProfile(name);
    	else return workers.get(name);
    }
    
    public static void setProfile(String name, WorkerProfile profile) {
    	workers.put(name, profile);
    }
	
	//
	// Job profiles
	//
	
	public static boolean isWorkNow(String name) {
		if(!isWorker(name)) return false;
		JobType current = getProfile(name).getCurrentJob();
		return !current.equals(JobType.JOBLESS);
	}
	
	public static boolean hasJobProfile(String name, JobType job) {
		return getProfile(name).hasJobProfile(job);
	}
	
	public static JobProfile getJobProfile(String name, JobType job) {
		return getProfile(name).getJobProfile(job);
	}
	
	public static void setJobProfile(String name, JobType job, JobProfile profile) {
		getProfile(name).setJobProfile(job, profile);
	}
	
	public static void removeJobProfile(String name, JobType job) {
		getProfile(name).removeJobProfile(job);
	}
	
	//
	// Job info
	//
	
	public static JobInfo getJobInfo(JobType job) {
		return jobinfo.get(job);
	}
	
	public static void setJobInfo(JobType job, JobInfo info) {
		jobinfo.put(job, info);
	}
	
	public static int getWorkersCount(JobType job) {
		return jobinfo.get(job).getWorkersCount();
	}
	
	//
	// Current job
	//
	
	public static JobType getCurrentJob(String name) {
		return getProfile(name).getCurrentJob();
	}
	
	public static void setCurrentJob(String name, JobType job) {
		getProfile(name).setCurrentJob(job);
	}
	
	public static void resetCurrentJob(String name) {
		getProfile(name).resetCurrentJob();
	}
	
}
