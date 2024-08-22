package eu.macsworks.fiverr.manhunt.listeners;

import eu.macsworks.fiverr.manhunt.Manhunt;
import eu.macsworks.fiverr.manhunt.managers.GameState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		switch (Manhunt.getInstance().getGameManager().getState()){
			case CLEANUP -> {
				event.getPlayer().kickPlayer("Server is restarting!");
			}

			case PLAYING -> {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Manhunt.getInstance(), () -> {
					event.getPlayer().setGameMode(GameMode.SPECTATOR);
					event.getPlayer().teleport(Manhunt.getInstance().getGameManager().getWorld().getSpawnLocation());
				}, 20L);
			}

			case WAITING -> {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Manhunt.getInstance(), () -> {
					event.getPlayer().setGameMode(GameMode.SURVIVAL);
					event.getPlayer().setMetadata("no-hit", new FixedMetadataValue(Manhunt.getInstance(), true));
					Manhunt.getInstance().getGameManager().addPlayer(event.getPlayer());
				}, 20L);
			}
		}
	}

}
