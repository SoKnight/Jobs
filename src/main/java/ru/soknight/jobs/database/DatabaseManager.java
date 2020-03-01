package ru.soknight.jobs.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.utils.Logger;

public class DatabaseManager {
	
	private ConnectionSource source;
	private Dao<WorkerProfile, String> workersDao;
	private Dao<JobProfile, Integer> jobsDao;
	private Dao<WorkspaceBlock, Integer> blocksDao;
	private Dao<WorkspaceBlocktype, Integer> blocktypesDao;
	
	public DatabaseManager(Database database) throws SQLException {
		source = database.getConnection();
		workersDao = DaoManager.createDao(source, WorkerProfile.class);
		jobsDao = DaoManager.createDao(source, JobProfile.class);
		
		blocksDao = DaoManager.createDao(source, WorkspaceBlock.class);
		blocktypesDao = DaoManager.createDao(source, WorkspaceBlocktype.class);
	}
	
	public void shutdown() {
		try {
			source.close();
			Logger.info("Database connection closed.");
		} catch (IOException e) {
			Logger.error("Failed close database connection: " + e.getLocalizedMessage());
		}
	}
	
	/*
	 * Worker profiles
	 */
	
	public boolean isWorker(String name) {
		return getProfile(name) != null;
	}
	
	public WorkerProfile getProfile(String name) {
		try {
			return workersDao.queryForId(name);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int saveProfile(WorkerProfile profile) {
		try {
			return workersDao.create(profile);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int updateProfile(WorkerProfile profile) {
		try {
			return workersDao.update(profile);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/*
	 * Job profiles
	 */
	
	public boolean hasJobProfile(String name, JobType job) {
		return getJobProfile(name, job) != null ? true : false;
	}
	
	public JobProfile getJobProfile(String name, JobType job) {
		try {
			QueryBuilder<JobProfile, Integer> builder = jobsDao.queryBuilder();
			Where<JobProfile, Integer> where = builder.where();
			
			where.eq("name", name);
			where.and();
			where.eq("job", job);
			
			JobProfile profile = builder.queryForFirst();
			return profile;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<JobProfile> getJobProfiles(String name) {
		try {
			QueryBuilder<JobProfile, Integer> builder = jobsDao.queryBuilder();
			Where<JobProfile, Integer> where = builder.where();
			
			where.eq("name", name);
			
			List<JobProfile> profiles = builder.query();
			return profiles;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveJobProfile(JobProfile profile) {
		try {
			jobsDao.createOrUpdate(profile);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeJobProfile(JobProfile profile) {
		try {
			jobsDao.delete(profile);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getWorkersCount(JobType job) {
		try {
			List<JobProfile> profiles = jobsDao.queryBuilder().where().eq("job", job).query();
			return profiles != null ? profiles.size() : 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/*
	 * Job blocks
	 */
	
	public JobType getJobForBlock(String world, int x, int y, int z) {
		try {
			QueryBuilder<WorkspaceBlock, Integer> builder = blocksDao.queryBuilder();
			Where<WorkspaceBlock, Integer> where = builder.where();
			
			where.eq("world", world);
			where.and();
			where.eq("x", x);
			where.and();
			where.eq("y", y);
			where.and();
			where.eq("z", z);
			
			WorkspaceBlock block = builder.queryForFirst();
			if(block != null) return block.getJob();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<WorkspaceBlock> getBlocks(JobType job) {
		try {
			return blocksDao.queryBuilder().where().eq("job", job).query();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean hasBlock(String world, int x, int y, int z) {
		JobType type = getJobForBlock(world, x, y, z);
		return type != null;
	}
	
	public void saveBlock(WorkspaceBlock block) {
		try {
			blocksDao.create(block);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeBlock(WorkspaceBlock block) {
		try {
			DeleteBuilder<WorkspaceBlock, Integer> builder = blocksDao.deleteBuilder();
			Where<WorkspaceBlock, Integer> where = builder.where();
			
			where.eq("job", block.getJob()).and();
			where.eq("world", block.getWorld()).and();
			where.eq("x", block.getX()).and();
			where.eq("y", block.getY()).and();
			where.eq("z", block.getZ());
			
			builder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Job blocktypes
	 */
	
	public List<Material> getBlocktypes(JobType job) {
		List<Material> materials = new ArrayList<>();
		
		try {
			QueryBuilder<WorkspaceBlocktype, Integer> builder = blocktypesDao.queryBuilder();
			Where<WorkspaceBlocktype, Integer> where = builder.where();
			
			where.eq("job", job);
			
			List<WorkspaceBlocktype> blocktypes = builder.query();
			
			if(blocktypes != null)
				for(WorkspaceBlocktype blocktype : blocktypes)
					materials.add(blocktype.getMaterial());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return materials;
	}
	
	public boolean hasBlocktype(JobType job, Material material) {
		try {
			QueryBuilder<WorkspaceBlocktype, Integer> builder = blocktypesDao.queryBuilder();
			Where<WorkspaceBlocktype, Integer> where = builder.where();
			
			where.eq("job", job);
			where.and();
			where.eq("material", material);
			
			WorkspaceBlocktype blocktype = builder.queryForFirst();
			
			return blocktype != null ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void saveBlocktype(WorkspaceBlocktype blocktype) {
		try {
			blocktypesDao.create(blocktype);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeBlocktype(WorkspaceBlocktype blocktype) {
		try {
			DeleteBuilder<WorkspaceBlocktype, Integer> builder = blocktypesDao.deleteBuilder();
			Where<WorkspaceBlocktype, Integer> where = builder.where();
				
			where.eq("job", blocktype.getJob()).and();
			where.eq("material", blocktype.getMaterial());
				
			builder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
