package ru.soknight.jobs.tool;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.lib.configuration.Messages;

@AllArgsConstructor
public class LevelUpHelper {

	private final Config config;
	private final Messages messages;
	
	public void sendLevelUpMessage(Player player, int level) {
		String title = messages.getFormatted("levelup.title", "%level%", level);
		String subtitle = messages.getFormatted("levelup.subtitle", "%level%", level);
		
		int in = config.getInt("levelup-title.fade-in", 20);
		int stay = config.getInt("levelup-title.stay", 60);
		int out = config.getInt("levelup-title.fade-out", 20);
		
		player.sendTitle(title, subtitle, in, stay, out);
	}
	
}
