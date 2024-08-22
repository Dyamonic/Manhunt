/**
 * Manhunt developed by Macs @ MacsWorks.eu in 2024
 **/

package eu.macsworks.fiverr.manhunt;

import eu.macsworks.fiverr.manhunt.commands.ForceStartCommand;
import eu.macsworks.fiverr.manhunt.commands.SetManhuntLobbyCommand;
import eu.macsworks.fiverr.manhunt.listeners.HitListener;
import eu.macsworks.fiverr.manhunt.listeners.ModifyListener;
import eu.macsworks.fiverr.manhunt.listeners.PlayerJoinListener;
import eu.macsworks.fiverr.manhunt.listeners.PlayerKillListener;
import eu.macsworks.fiverr.manhunt.managers.GameManager;
import eu.macsworks.fiverr.manhunt.utils.PluginLoader;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Manhunt extends JavaPlugin {

	private static Manhunt instance = null;

	public static Manhunt getInstance() {
		return Manhunt.instance;
	}

	private static void setInstance(Manhunt in) {
		Manhunt.instance = in;
	}

	@Getter
	private PluginLoader macsPluginLoader;

	@Getter
	private GameManager gameManager;

	@Override
	public void onEnable() {
		setInstance(this);

		macsPluginLoader = new PluginLoader();
		macsPluginLoader.load();

		gameManager = new GameManager();

		loadTasks();
		loadEvents();
		loadCommands();

		Bukkit.getLogger().info("--------------------------------------");
		Bukkit.getLogger().info("Manhunt was made by the team at macsworks.eu!");
		Bukkit.getLogger().info("--------------------------------------");
	}

	private void loadTasks() {
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			gameManager.tick();
		}, 0L, 20L);
	}

	private void loadEvents() {
		Bukkit.getPluginManager().registerEvents(new PlayerKillListener(), this);
		Bukkit.getPluginManager().registerEvents(new ModifyListener(), this);
		Bukkit.getPluginManager().registerEvents(new HitListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
	}

	private void loadCommands() {
		Objects.requireNonNull(getCommand("setmanhuntlobby")).setExecutor(new SetManhuntLobbyCommand());
		Objects.requireNonNull(getCommand("forcestart")).setExecutor(new ForceStartCommand());
	}

	@Override
	public void onDisable() {
		macsPluginLoader.save();

		Bukkit.getLogger().info("--------------------------------------");
		Bukkit.getLogger().info("Manhunt was made by the team at macsworks.eu!");
		Bukkit.getLogger().info("--------------------------------------");
	}
}
