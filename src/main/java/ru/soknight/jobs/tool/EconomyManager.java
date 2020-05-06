package ru.soknight.jobs.tool;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

import ru.soknight.jobs.Jobs;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.format.FloatFormatter;
import ru.soknight.peconomy.PEcoAPI;
import ru.soknight.peconomy.PEconomy;
import ru.soknight.peconomy.configuration.CurrencyInstance;
import ru.soknight.peconomy.database.Wallet;

public class EconomyManager {

	private final Logger logger;
	private final Messages messages;
	
	private final FloatFormatter formatter;
	
	private PEcoAPI api;
	
	private boolean isBootstrapped;
	
	public EconomyManager(Jobs plugin, Messages messages) {
		this.logger = plugin.getLogger();
		this.messages = messages;
		this.formatter = new FloatFormatter('.');
		update();
	}
	
	public void update() {
		try {
			this.api = PEconomy.getAPI();
			this.isBootstrapped = api != null;
			logger.info("Economy manager bootstrapped.");
		} catch (Exception e) {
			logger.severe("Failed to bootstrap economy manager. Check if PEconomy is installed.");
		}
	}
	
	public void sendSalary(Player player, JobConfiguration jobConfig, float salary) {
		if(!isBootstrapped) return;
		
		String name = player.getName();
		
		String currencyid = jobConfig.getSalaryCurrency();
		CurrencyInstance currency = api.getCurrencyByID(currencyid);
		
		String symbol = currency != null ? currency.getSymbol() : "?";
		
		if(currency != null) {
			float limit = currency.getLimit();
			float balance = api.getAmount(name, currencyid);
			
			if(balance + salary > limit) {
				messages.getAndSend(player, "salary.limit");
				return;
			}
		}
		
		Wallet wallet = api.addAmount(name, currencyid, salary);
		api.updateWallet(wallet);
		
		messages.sendFormatted(player, "salary.earned",
				"%salary%", formatter.shortToString(salary, 2),
				"%currency%", symbol);
	}
	
}
