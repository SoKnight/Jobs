package ru.soknight.jobs.handlers;

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
import org.bukkit.inventory.ItemStack;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobInfo;
import ru.soknight.jobs.database.JobProfile;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.units.JobBlock;
import ru.soknight.jobs.units.JobEntity;
import ru.soknight.jobs.utils.InfoSender;

public class JobProcessHandler implements Listener {
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player breaker = e.getPlayer();
		String name = breaker.getName();
		if(!DatabaseManager.isWorkNow(name)) return;
		
		JobType job = DatabaseManager.getProfile(name).getCurrentJob();
		if(!(job == JobType.MINER || job == JobType.WOODCUTTER || job == JobType.FARMER)) return;
		
		Block block = e.getBlock();
		Location loc = block.getLocation();
		if(!DatabaseManager.hasBlock(loc, job)) { e.setCancelled(true); return; }
		
		e.setExpToDrop(0);
		e.setDropItems(false);
		
		Material material = block.getType();
		if(material.equals(Material.BEDROCK)) { e.setCancelled(true); return; }
		
		JobInfo info = DatabaseManager.getJobInfo(job);
		JobProfile profile = DatabaseManager.getJobProfile(name, job);
		
		int nextlevel = profile.addProgress(1);
		if(nextlevel != -1) InfoSender.sendLevelup(breaker, nextlevel);
		
		JobBlock jobBlock = new JobBlock(material, block.getBlockData(), loc);
		if(job.equals(JobType.MINER)) {
			e.setCancelled(true);
			jobBlock.placeBedrock();
		}
		JobTasksHandler.addBlock(job, jobBlock);
		
		double salary = info.getFinalSalary(breaker, profile.getLevel(), material);
		InfoSender.sendSalary(breaker, salary);
	}
	
	@EventHandler
	public void onFishing(PlayerFishEvent e) {
		Player fisherman = e.getPlayer();
		String name = fisherman.getName();
		if(!DatabaseManager.isWorkNow(name)) return;
		
		JobType job = DatabaseManager.getCurrentJob(name);
		if(!job.equals(JobType.FISHERMAN)) return;
		
		State state = e.getState();
		if(!state.equals(State.CAUGHT_FISH)) return;
		
		Entity caught = e.getCaught();
		if(!caught.getType().equals(EntityType.DROPPED_ITEM)) return;
		
		e.setExpToDrop(0);
		Item item = (Item) caught;
		
		Material material = item.getItemStack().getType();
		item.remove();
		
		JobInfo info = DatabaseManager.getJobInfo(job);
		JobProfile profile = DatabaseManager.getJobProfile(name, job);
		
		int nextlevel = profile.addProgress(1);
		if(nextlevel != -1) InfoSender.sendLevelup(fisherman, nextlevel);
		
		double salary = info.getFinalSalary(fisherman, profile.getLevel(), material);
		InfoSender.sendSalary(fisherman, salary);
	}
	
	@EventHandler
	public void onHunting(EntityDeathEvent e) {
		LivingEntity death = e.getEntity();
		Player hunter = death.getKiller();
		if(hunter == null) return;
		
		String name = hunter.getName();
		if(!DatabaseManager.isWorkNow(name)) return;
		
		JobType job = DatabaseManager.getCurrentJob(name);
		if(!job.equals(JobType.HUNTER)) return;
		
		JobInfo info = DatabaseManager.getJobInfo(job);
		JobProfile profile = DatabaseManager.getJobProfile(name, job);
		
		ItemStack drop = getDrop(death.getType());
		double boost = info.getBoost(drop.getType());
		
		int nextlevel = profile.addProgress(1);
		if(nextlevel != -1) InfoSender.sendLevelup(hunter, nextlevel);
		
		e.setDroppedExp(0);
		e.getDrops().clear();
		
		JobEntity jobEntity = new JobEntity(death.getType(), death.getLocation());
		JobTasksHandler.addEntity(job, jobEntity);
		
		double salary = info.getFinalSalary(hunter, profile.getLevel(), boost);
		InfoSender.sendSalary(hunter, salary);
	}
	
	private static ItemStack getDrop(EntityType mob) {
		switch (mob) {
		case COW: { return new ItemStack(Material.BEEF, 1); }
		case CHICKEN: { return new ItemStack(Material.CHICKEN, 1); }
		case PIG: { return new ItemStack(Material.PORKCHOP, 1); }
		case SHEEP: { return new ItemStack(Material.MUTTON, 1); }
		case RABBIT: { return new ItemStack(Material.RABBIT, 1); }
		default:
			return null;
		}
	}
	
}
