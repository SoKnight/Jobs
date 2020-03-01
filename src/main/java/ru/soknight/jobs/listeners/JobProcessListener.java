package ru.soknight.jobs.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import lombok.RequiredArgsConstructor;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobProfile;
import ru.soknight.jobs.database.WorkerProfile;
import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.handlers.InfoSender;
import ru.soknight.jobs.handlers.JobTasksHandler;
import ru.soknight.jobs.objects.JobBlock;
import ru.soknight.jobs.objects.JobConfig;
import ru.soknight.jobs.objects.JobEntity;

@RequiredArgsConstructor
public class JobProcessListener implements Listener {
	
	private final DatabaseManager dbm;
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player breaker = e.getPlayer();
		String name = breaker.getName();
		
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null) return;
		
		JobType job = worker.getCurrentJob();
		if(job == null) return;
		
		if(!(job == JobType.MINER || job == JobType.WOODCUTTER || job == JobType.FARMER)) return;
		
		Block block = e.getBlock();
		Material material = block.getType();
		Location loc = block.getLocation();
		
		JobConfig config;
		try {
			config = Config.getJobConfig(job);
		} catch (NotLoadedConfigException e1) {
			return;
		}
		
		String world = loc.getWorld().getName();
		JobType jobblock = dbm.getJobForBlock(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(!dbm.hasBlocktype(job, material) && (jobblock == null || !jobblock.equals(job))) {
			e.setCancelled(true);
			return;
		}
		
		e.setExpToDrop(0);
		e.setDropItems(false);
		
		JobProfile profile = dbm.getJobProfile(name, job);
		if(profile == null) return;
		
		try {
			int nextlevel = profile.addProgress(1);
			if(nextlevel != -1)
				InfoSender.sendLevelup(breaker, nextlevel);
			
			dbm.saveJobProfile(profile);
			
			JobBlock jobBlock = new JobBlock(job, material, block.getBlockData(), loc);
			JobTasksHandler.addBlock(job, jobBlock);
		} catch (NotLoadedConfigException e1) {
			return;
		}
		
		double salary = config.getFinalSalary(breaker, profile.getLevel(), material);
		InfoSender.sendSalary(breaker, salary);
	}
	
	@EventHandler
	public void onFishing(PlayerFishEvent e) {
		Player fisherman = e.getPlayer();
		String name = fisherman.getName();
		
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null) return;
		
		JobType job = worker.getCurrentJob();
		if(job == null || !job.equals(JobType.FISHERMAN)) return;
		
		State state = e.getState();
		if(!state.equals(State.CAUGHT_FISH)) return;
		
		Entity caught = e.getCaught();
		if(!caught.getType().equals(EntityType.DROPPED_ITEM)) return;
		
		e.setExpToDrop(0);
		Item item = (Item) caught;
		
		Material material = item.getItemStack().getType();
		item.remove();
		
		JobConfig config;
		try {
			config = Config.getJobConfig(job);
		} catch (NotLoadedConfigException e1) {
			return;
		}
		
		JobProfile profile = dbm.getJobProfile(name, job);
		if(profile == null) return;
		
		try {
			int nextlevel = profile.addProgress(1);
			if(nextlevel != -1)
				InfoSender.sendLevelup(fisherman, nextlevel);
			
			dbm.saveJobProfile(profile);
		} catch (NotLoadedConfigException e1) {
			return;
		}
		
		double salary = config.getFinalSalary(fisherman, profile.getLevel(), material);
		InfoSender.sendSalary(fisherman, salary);
	}
	
	@EventHandler
	public void onHunting(EntityDeathEvent e) {
		LivingEntity death = e.getEntity();
		Player hunter = death.getKiller();
		if(hunter == null) return;
		
		String name = hunter.getName();
		
		WorkerProfile worker = dbm.getProfile(name);
		if(worker == null) return;
		
		JobType job = worker.getCurrentJob();
		if(job == null || !job.equals(JobType.HUNTER)) return;
		
		JobConfig config;
		try {
			config = Config.getJobConfig(job);
		} catch (NotLoadedConfigException e1) {
			return;
		}
		
		JobProfile profile = dbm.getJobProfile(name, job);
		if(profile == null) return;
		
		try {
			int nextlevel = profile.addProgress(1);
			if(nextlevel != -1)
				InfoSender.sendLevelup(hunter, nextlevel);
			
			dbm.saveJobProfile(profile);
			
			e.setDroppedExp(0);
			e.getDrops().clear();
		
			JobEntity jobEntity = new JobEntity(death.getType(), death.getLocation());
			JobTasksHandler.addEntity(job, jobEntity);
		} catch (NotLoadedConfigException e1) {
			return;
		}
		
		double salary = config.getFinalSalary(hunter, profile.getLevel(), death.getType());
		InfoSender.sendSalary(hunter, salary);
	}
	
}
