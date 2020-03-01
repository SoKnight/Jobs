package ru.soknight.jobs.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.jobs.files.Messages;

public class Requirements {

	private static List<String> jobs = Arrays.asList("miner", "woodcutter", "hunter", "fisherman", "farmer");
	
	public static boolean hasPermission(CommandSender sender, String permission) {
		if(!sender.hasPermission(permission)) {
			sender.sendMessage(Messages.getMessage("error-no-permission"));
			return false;
		} else return true;
	}
	
	public static boolean isPlayer(CommandSender sender) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.getMessage("error-only-for-players"));
			return false;
		} else return true;
	}
	
	public static boolean playerExist(CommandSender sender, String nickname) {
		for(Player p : Bukkit.getOnlinePlayers())
			if(p.getName().equals(nickname)) return true;
		for(OfflinePlayer op : Bukkit.getOfflinePlayers())
			if(op.getName().equals(nickname)) return true;
		sender.sendMessage(Messages.getMessage("error-player-not-found").replace("%nickname%", nickname));
		return false;
	}
	
	public static boolean jobExist(CommandSender sender, String job) {
		if(!jobs.contains(job.toLowerCase())) {
			sender.sendMessage(Messages.getMessage("error-job-not-found").replace("%job%", job));
			return false;
		} else return true;
	}
	
	public static boolean isInvalidUsage(CommandSender sender, String[] args, int neededargscount) {
		if(args.length < neededargscount) {
			sender.sendMessage(Messages.getMessage("error-invalid-syntax"));
			return true;
		} else return false;
	}
	
	public static boolean argIsInteger(CommandSender sender, String arg) {
		try {
			Integer.parseInt(arg);
			return true;
		} catch (NumberFormatException e) {
			sender.sendMessage(Messages.getMessage("error-arg-is-not-int").replace("%arg%", arg));
			return false;
		}
	}
	
}
