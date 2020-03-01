package ru.soknight.jobs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;
import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.enums.JobType;

@RequiredArgsConstructor
public class CommandsTabCompleter implements TabCompleter {
	
	private final DatabaseManager dbm;
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) return null;
		
		TabCompletionHelper helper = new TabCompletionHelper(dbm);
		if(args.length < 2) {
			helper.putSubcommands(args[0].toLowerCase());
			return helper.getCompletions();
		}
		
		String arg = args[1].toLowerCase();
		String name = sender.getName();
		
		switch(args[0]) {
		case "join":
			if(args.length == 2)
				helper.putAvailableJobs(arg, name);
			break;
		case "leave":
			if(args.length == 2)
				helper.putJoinedJobs(arg, name);
			break;
		case "listtypes":
			if(args.length == 2)
				helper.putAllJobs(arg);
			break;
		case "listblocks":
			if(args.length == 2)
				helper.putAllJobs(arg);
			break;
		case "info":
			if(args.length == 2)
				helper.putJoinedJobs(arg, name);
			if(sender.hasPermission("jobs.info.other") && args.length == 3) {
				List<String> output = new ArrayList<>();
				arg = args[2].toLowerCase();
				
				for(Player p : Bukkit.getServer().getOnlinePlayers())
					if(p.getName().toLowerCase().startsWith(arg)) output.add(p.getName());
				for(OfflinePlayer op : Bukkit.getServer().getOfflinePlayers())
					if(op.getName().toLowerCase().startsWith(arg)) output.add(op.getName());
			}
			break;
		case "start":
			if(args.length == 2)
				helper.putJoinedJobs(arg, name);
			break;
		case "edit":
			if(args.length == 2)
				helper.putAllJobs(arg);
			break;
		case "addblocktype":
			if(args.length == 2)
				helper.putAllJobs(arg);
			else if(args.length == 3) {
				arg = args[2].toLowerCase();
				List<String> output = new ArrayList<>();
				
				JobType job = JobType.valueOf(args[1].toUpperCase());
				if(job == null) break;
				
				for(Material material : Material.values()) {
					if(material.isLegacy()) continue;
					String m = material.name().toLowerCase();
					if(m.startsWith(arg))
						output.add(m);
				}
				return output;
			}
			break;
		case "remblocktype":
			if(args.length == 2)
				helper.putAllJobs(arg);
			else if(args.length == 3) {
				arg = args[2].toLowerCase();
				List<String> output = new ArrayList<>();
				
				JobType job = JobType.valueOf(args[1].toUpperCase());
				if(job == null) break;
				
				List<Material> materials = dbm.getBlocktypes(job);
				if(materials.isEmpty()) break;
				
				for(Material material : materials) {
					String m = material.name().toLowerCase();
					if(m.startsWith(arg))
						output.add(m);
				}
				return output;
			}
			break;
		default:
			break;
		}
		
		return helper.getCompletions();
	}

}
