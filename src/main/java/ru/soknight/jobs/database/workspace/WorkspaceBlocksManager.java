package ru.soknight.jobs.database.workspace;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.jobs.database.Database;

public class WorkspaceBlocksManager {

	private final Logger logger;
	private final ConnectionSource source;
	
	private final Dao<WorkspaceLinkedBlock, Integer> blocksDao;
	
	public WorkspaceBlocksManager(Jobs plugin, Database database) throws SQLException {
		this.logger = plugin.getLogger();
		this.source = database.getConnection();
		
		this.blocksDao = DaoManager.createDao(source, WorkspaceLinkedBlock.class);
	}
	
	public void shutdown() {
		try {
			this.source.close();
			logger.info("[WorkspaceTilesManager] Disconnected from database.");
		} catch (IOException e) {
			logger.severe("[WorkspaceTilesManager] Failed to close database connection: " + e.getMessage());
		}
	}
	
	/*
	 * Job linked blocks
	 */
	
	public JobTypeEnum getJobForLinkedBlock(String world, int x, int y, int z) {
		try {
			QueryBuilder<WorkspaceLinkedBlock, Integer> builder = blocksDao.queryBuilder();
			Where<WorkspaceLinkedBlock, Integer> where = builder.where();
			
			where.eq("world", world);
			where.and();
			where.eq("x", x);
			where.and();
			where.eq("y", y);
			where.and();
			where.eq("z", z);
			
			WorkspaceLinkedBlock block = builder.queryForFirst();
			return block != null ? block.getJob() : null;
		} catch (SQLException e) {
			logger.severe("Failed to check linked job for any block: " + e.getMessage());
			return null;
		}
	}
	
	public List<WorkspaceLinkedBlock> getLinkedBlocks(JobTypeEnum jobType) {
		try {
			QueryBuilder<WorkspaceLinkedBlock, Integer> builder = blocksDao.queryBuilder();
			Where<WorkspaceLinkedBlock, Integer> where = builder.where();
			
			where.eq("job", jobType);
			
			return builder.query();
		} catch (SQLException e) {
			logger.severe("Failed to get linked with job " + jobType.getDefaultName() + " blocks: " + e.getMessage());
			return null;
		}
	}
	
	public boolean isLinkedBlock(String world, int x, int y, int z) {
		return getJobForLinkedBlock(world, x, y, z) != null;
	}
	
	public boolean saveLinkedBlock(WorkspaceLinkedBlock block) {
		try {
			return blocksDao.create(block) != 0;
		} catch (SQLException e) {
			logger.severe("Failed to save linked block: " + e.getMessage());
			return false;
		}
	}
	
	public boolean removeBlock(WorkspaceLinkedBlock block) {
		try {
			DeleteBuilder<WorkspaceLinkedBlock, Integer> builder = blocksDao.deleteBuilder();
			Where<WorkspaceLinkedBlock, Integer> where = builder.where();
			
			where.eq("job", block.getJob()).and();
			where.eq("world", block.getWorld()).and();
			where.eq("x", block.getX()).and();
			where.eq("y", block.getY()).and();
			where.eq("z", block.getZ());
			
			return builder.delete() != 0;
		} catch (SQLException e) {
			logger.severe("Failed to remove linked block: " + e.getMessage());
			return false;
		}
	}
	
}
