package ru.soknight.jobs.database.profile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.Database;

public class ProfilesManager {
	
	private final Logger logger;
	private final ConnectionSource source;
	
	private final Dao<PlayerProfile, String> profilesDao;
	private final Dao<EmployeeProfile, Integer> employeesDao;
	
	public ProfilesManager(Jobs plugin, Database database) throws SQLException {
		this.logger = plugin.getLogger();
		this.source = database.getConnection();
		
		this.profilesDao = DaoManager.createDao(source, PlayerProfile.class);
		this.employeesDao = DaoManager.createDao(source, EmployeeProfile.class);
	}
	
	public void shutdown() {
		try {
			this.source.close();
			logger.info("[ProfilesManager] Disconnected from database.");
		} catch (IOException e) {
			logger.severe("[ProfilesManager] Failed to close database connection: " + e.getMessage());
		}
	}
	
	/*
	 * Players profiles
	 */
	
	public boolean createProfile(PlayerProfile profile) {
		try {
			return profilesDao.create(profile) != 0;
		} catch (SQLException e) {
			logger.severe("Failed to create profile for " + profile.getPlayer() + ": " + e.getMessage());
			return false;
		}
	}
	
	public PlayerProfile getProfile(String player) {
		try {
			return profilesDao.queryForId(player);
		} catch (SQLException e) {
			logger.severe("Failed to get profile of " + player + ": " + e.getMessage());
			return null;
		}
	}
	
	public boolean hasProfile(String player) {
		return getProfile(player) != null;
	}
	
	public boolean updateProfile(PlayerProfile profile) {
		try {
			return profilesDao.update(profile) != 0;
		} catch (SQLException e) {
			logger.severe("Failed to update profile for " + profile.getPlayer() + ": " + e.getMessage());
			return false;
		}
	}
	
	/*
	 * Employees profiles
	 */
	
	public EmployeeProfile getEmployeeProfile(String worker, JobTypeEnum jobType) {
		try {
			QueryBuilder<EmployeeProfile, Integer> builder = employeesDao.queryBuilder();
			Where<EmployeeProfile, Integer> where = builder.where();
			
			where.eq("worker", worker);
			where.and();
			where.eq("jobType", jobType);
			
			return builder.queryForFirst();
		} catch (SQLException e) {
			logger.severe("Failed to get employee profile of " + worker + ": " + e.getMessage());
			return null;
		}
	}
	
	public List<EmployeeProfile> getEmployeeProfiles(String worker) {
		try {
			QueryBuilder<EmployeeProfile, Integer> builder = employeesDao.queryBuilder();
			Where<EmployeeProfile, Integer> where = builder.where();
			
			where.eq("worker", worker);
			
			return builder.query();
		} catch (SQLException e) {
			logger.severe("Failed to get employee profiles of " + worker + ": " + e.getMessage());
			return null;
		}
	}
	
	public long getEmployeesCount(JobTypeEnum jobType) {
		try {
			QueryBuilder<EmployeeProfile, Integer> builder = employeesDao.queryBuilder();
			Where<EmployeeProfile, Integer> where = builder.where();
			
			where.eq("jobType", jobType);
			
			builder.setCountOf(true);
			
			return employeesDao.countOf(builder.prepare());
		} catch (SQLException e) {
			logger.severe("Failed to get employees count of " + jobType.getDefaultName() + ": " + e.getMessage());
			return 0;
		}
	}
	
	public long getTotalEmployeesCount() {
		try {
			QueryBuilder<EmployeeProfile, Integer> builder = employeesDao.queryBuilder();
			builder.setCountOf(true);
			
			return employeesDao.countOf(builder.prepare());
		} catch (SQLException e) {
			logger.severe("Failed to get total employees count: " + e.getMessage());
			return 0;
		}
	}
	
	public boolean hasEmployeeProfile(String worker, JobTypeEnum jobType) {
		return getEmployeeProfile(worker, jobType) != null;
	}
	
	public boolean saveEmployeeProfile(EmployeeProfile profile) {
		try {
			CreateOrUpdateStatus status = employeesDao.createOrUpdate(profile);
			return status.isCreated() || status.isUpdated();
		} catch (SQLException e) {
			logger.severe("Failed to save employee profile for " + profile.getWorker() + ": " + e.getMessage());
			return false;
		}
	}
	
	public boolean removeEmployeeProfile(EmployeeProfile profile) {
		try {
			return employeesDao.delete(profile) != 0;
		} catch (SQLException e) {
			logger.severe("Failed to remove employee profile for " + profile.getWorker() + ": " + e.getMessage());
			return false;
		}
	}

}
