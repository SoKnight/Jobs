package ru.soknight.jobs.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.handlers.SelectionToolsHandler;
import ru.soknight.jobs.units.SelectionTool;
import ru.soknight.jobs.units.SelectionTool.Operation;
import ru.soknight.jobs.utils.Requirements;
import ru.soknight.jobs.utils.Utils;

public class CommandRemtool {
	
	public static void execute(CommandSender sender, String[] args) {
		if(!Requirements.isPlayer(sender)) return;
		if(Requirements.isInvalidUsage(sender, args, 2)) return;
		if(!Requirements.jobExist(sender, args[1])) return;
		
		Player p = (Player) sender;
		Inventory inv = p.getInventory();
		
		if(inv.firstEmpty() == -1) {
			p.sendMessage(Messages.getMessage("error-full-inventory"));
			return; }
		
		JobType job = JobType.valueOf(args[1].toUpperCase());
		SelectionTool tool = SelectionToolsHandler.addTool(job, Operation.REMOVE);
		ItemStack item = tool.getItemStack();
		
		inv.addItem(item);
		p.sendMessage(Utils.pformat("select-remove-tool", "job@@" + job.getName()));
		return;
	}
	
}
