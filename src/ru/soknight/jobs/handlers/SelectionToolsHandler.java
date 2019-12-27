package ru.soknight.jobs.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.units.SelectionTool;
import ru.soknight.jobs.units.SelectionTool.Operation;
import ru.soknight.jobs.utils.Utils;

public class SelectionToolsHandler implements Listener {

	private static ItemStack defaddtool, defremtool;
	private static Map<ItemStack, SelectionTool> tools = new HashMap<>();
	
	@EventHandler
	public void onUse(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(!p.hasPermission("jobs.usetools")) return;
		if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if(!e.getHand().equals(EquipmentSlot.HAND)) return;
		
		ItemStack inhand = e.getItem();
		if(inhand == null) return;
		if(!hasTool(inhand)) return;
		
		Location location = e.getClickedBlock().getLocation();
		SelectionTool tool = getTool(inhand);
		
		Operation operation = tool.getOperation();
		JobType job = tool.getJob();
		String jobname = job.getName();
		
		if(operation.equals(Operation.ADD)) {
			if(DatabaseManager.hasBlock(location, job)) {
				p.sendActionBar(Utils.pformat("select-block-add-failed", "job@@" + jobname));
				return; }
			DatabaseManager.addBlock(location, job);
			String loc = Utils.formatLocation(location);
			p.sendActionBar(Utils.pformat("select-block-add-success", "location@@" + loc, "job@@" + jobname));
		} else {
			if(!DatabaseManager.hasBlock(location, job)) {
				p.sendActionBar(Utils.pformat("select-block-remove-failed", "job@@" + jobname));
				return; }
			DatabaseManager.removeBlock(location, job);
			String loc = Utils.formatLocation(location);
			p.sendActionBar(Utils.pformat("select-block-remove-success", "location@@" + loc, "job@@" + jobname));
		}
		
	}
	
	public static SelectionTool addTool(JobType job, Operation operation) {
		ItemStack item;
		if(operation.equals(Operation.ADD)) item = defaddtool;
		else item = defremtool;
		
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		meta.getLore().forEach(s -> lore.add(s.replace("%job%", job.getName())));
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		SelectionTool tool = new SelectionTool(item, operation, job);
		tools.put(item, tool);
		return tool;
	}
	
	private static boolean hasTool(ItemStack item) {
		return tools.containsKey(item);
	}
	
	public static SelectionTool getTool(ItemStack item) {
		return tools.get(item);
	}
	
	public static void refreshDefaults() {
		ConfigurationSection main = Config.config.getConfigurationSection("tools");
		ConfigurationSection sadd = main.getConfigurationSection("add-tool");
		ConfigurationSection srem = main.getConfigurationSection("remove-tool");
		
		String addname = sadd.getString("name").replace("&", "\u00A7");
		String remname = srem.getString("name").replace("&", "\u00A7");
		
		Material addmaterial = Material.valueOf(sadd.getString("material").toUpperCase());
		Material remmaterial = Material.valueOf(srem.getString("material").toUpperCase());
		
		boolean addenchanted = sadd.getBoolean("enchanted");
		boolean remenchanted = srem.getBoolean("enchanted");
		
		List<String> addlore = Utils.fixListColors(sadd.getStringList("lore"));
		List<String> remlore = Utils.fixListColors(srem.getStringList("lore"));
		
		defaddtool = new ItemStack(addmaterial, 1);
		defremtool = new ItemStack(remmaterial, 1);
		
		ItemMeta addmeta = defaddtool.getItemMeta();
		ItemMeta remmeta = defremtool.getItemMeta();
		
		addmeta.setDisplayName(addname);
		remmeta.setDisplayName(remname);
		
		addmeta.setLore(addlore);
		remmeta.setLore(remlore);
		
		defaddtool.setItemMeta(addmeta);
		defremtool.setItemMeta(remmeta);
		
		if(addenchanted) {
			defaddtool.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
			defaddtool.addItemFlags(ItemFlag.HIDE_ENCHANTS); }
		if(remenchanted) {
			defremtool.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
			defremtool.addItemFlags(ItemFlag.HIDE_ENCHANTS); }
	}
	
	
}
