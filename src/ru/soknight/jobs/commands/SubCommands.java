package ru.soknight.jobs.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ru.soknight.jobs.database.DatabaseManager;
import ru.soknight.jobs.database.JobType;
import ru.soknight.jobs.files.Messages;
import ru.soknight.jobs.utils.Requirements;
import ru.soknight.jobs.utils.Utils;

public class SubCommands implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Messages.getMessage("error-without-args"));
			return true; }
		
		switch(args[0]) {
		case "addtool":
			if(Requirements.hasPermission(sender, "jobs.addtool")) CommandAddtool.execute(sender, args);
			break;
		case "done":
			if(Requirements.hasPermission(sender, "jobs.done")) CommandDone.execute(sender);
			break;
		case "help":
			if(Requirements.hasPermission(sender, "jobs.help")) CommandHelp.execute(sender);
			break;
		case "join":
			if(Requirements.hasPermission(sender, "jobs.join")) CommandJoin.execute(sender, args);
			break;
		case "leave":
			if(Requirements.hasPermission(sender, "jobs.leave")) CommandLeave.execute(sender, args);
			break;
		case "info":
			if(Requirements.hasPermission(sender, "jobs.info")) CommandInfo.execute(sender, args);
			break;
		case "list":
			if(Requirements.hasPermission(sender, "jobs.list")) CommandList.execute(sender);
			break;
		case "reload":
			if(Requirements.hasPermission(sender, "jobs.reload")) CommandReload.execute(sender);
			break;
		case "remtool":
			if(Requirements.hasPermission(sender, "jobs.remtool")) CommandRemtool.execute(sender, args);
			break;
		case "start":
			if(Requirements.hasPermission(sender, "jobs.start")) CommandStart.execute(sender, args);
			break;
		default:
			sender.sendMessage(Messages.getMessage("error-command-not-found"));
			break;
		}
		return true;
	}
	
	public static final List<String> subcommands = Arrays.asList("addtool", "done", "help", "join", "leave", 
			"info", "list", "reload", "remtool", "spawnmob", "start");
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) return null;
		List<String> output = new ArrayList<>();
		if(args.length == 1) {
			for(String s : subcommands)
				if(s.startsWith(args[0].toLowerCase())) 
					output.add(s);
			return output;
		}
		
		switch(args[0]) {
		case "addtool":
			if(args.length == 2)
				for(JobType job : Utils.jobs) {
					String joblower = job.name().toLowerCase();
					if(joblower.startsWith(args[1].toLowerCase())) 
						output.add(joblower); }
			break;
		case "join":
			if(args.length == 2) {
				String s = args[1].toLowerCase(), n = sender.getName();
				for(JobType job : Utils.jobs) {
					String joblower = job.name().toLowerCase();
					if(joblower.startsWith(s) && !DatabaseManager.hasJobProfile(n, job)) 
						output.add(joblower); }
			}
			if(sender.hasPermission("jobs.join.other") && args.length == 3)
				output.addAll(getPlayers(args[2]));
			break;
		case "leave":
			if(args.length == 2) {
				String s = args[1].toLowerCase(), n = sender.getName();
				for(JobType job : Utils.jobs) {
					String joblower = job.name().toLowerCase();
					if(joblower.startsWith(s) && DatabaseManager.hasJobProfile(n, job)) 
						output.add(joblower); }
			}
			if(sender.hasPermission("jobs.leave.other") && args.length == 3)
				output.addAll(getPlayers(args[2]));
			break;
		case "info":
			if(args.length == 2)
				for(JobType job : Utils.jobs) {
					String joblower = job.name().toLowerCase();
					if(joblower.startsWith(args[1].toLowerCase())) 
						output.add(joblower); }
			if(sender.hasPermission("jobs.info.other") && args.length == 3) 
				output.addAll(getPlayers(args[2]));
			break;
		case "remtool":
			if(args.length == 2)
				for(JobType job : Utils.jobs) {
					String joblower = job.name().toLowerCase();
					if(joblower.startsWith(args[1].toLowerCase())) 
						output.add(joblower); }
			break;
		case "start":
			if(args.length == 2) {
				String s = args[1].toLowerCase(), n = sender.getName();
				for(JobType job : Utils.jobs) {
					String joblower = job.name().toLowerCase();
					if(joblower.startsWith(s) && DatabaseManager.hasJobProfile(n, job)) 
						output.add(joblower); }
			}
			break;
		default:
			break;
		}
		return output;
	}
	
	private List<String> getPlayers(String arg) {
		List<String> output = new ArrayList<>();
		String s = arg.toLowerCase();
		for(Player p : Bukkit.getServer().getOnlinePlayers())
			if(p.getName().toLowerCase().startsWith(s)) output.add(p.getName());
		for(OfflinePlayer op : Bukkit.getServer().getOfflinePlayers())
			if(op.getName().toLowerCase().startsWith(s)) output.add(op.getName());
		return output;
	}

}
