package ru.soknight.jobs.handlers;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import com.destroystokyo.paper.Title;

import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.files.Messages;
import ru.soknight.peconomy.PEcoAPI;

public class InfoSender {

	private static boolean useActionbar, useTitle;
	private static String salarymessage, leveluptitle, levelupmessage;
	private static DecimalFormat df = new DecimalFormat("#0.00");
	
	public static void sendSalary(Player target, double salary) {
		PEcoAPI.addAmount(target.getName(), (float) salary, "dollars");
		String output = salarymessage.replace("%money%", df.format(salary));
		if(useActionbar) target.sendActionBar(output);
		else target.sendMessage(output);
	}
	
	public static void sendLevelup(Player target, int nextlevel) {
		String next = String.valueOf(nextlevel);
		String output = levelupmessage.replace("%level%", next);
		if(useTitle) {
			String titlestr = leveluptitle.replace("%level%", next);
			Title title = new Title(titlestr, output);
			target.sendTitle(title);
		} else target.sendMessage(output);
	}
	
	public static void refresh() {
		useTitle = Config.getConfig().getBoolean("messages.titled-levelup");
		useActionbar = Config.getConfig().getBoolean("messages.salary-actionbar");
		salarymessage = Messages.getRawMessage("salary-message");
		leveluptitle = Messages.getRawMessage("levelup-title");
		levelupmessage = Messages.getRawMessage("levelup-message");
	}
	
}
