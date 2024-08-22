package eu.macsworks.fiverr.manhunt.listeners;

import eu.macsworks.fiverr.manhunt.Manhunt;
import org.bukkit.GameMode;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerKillListener implements Listener {

	@EventHandler
	public void onChaserDeath(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player victim)) return;

		if(Manhunt.getInstance().getGameManager().isRunner(victim)) return;

		if(victim.getHealth() - event.getDamage() > 0) return;

		event.setCancelled(true);
		victim.setHealth(20D);
		Manhunt.getInstance().getGameManager().respawnChaser(victim);
	}

	@EventHandler
	public void onRunnerDeath(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player victim)) return;

		if(!Manhunt.getInstance().getGameManager().isRunner(victim)) return;

		if(victim.getHealth() - event.getDamage() > 0) return;

		event.setCancelled(true);
		victim.setHealth(20D);
		victim.setGameMode(GameMode.SPECTATOR);
		Manhunt.getInstance().getGameManager().killPlayer(victim);
	}

	@EventHandler
	public void onDragonDeath(EntityDamageEvent event){
		if(!(event.getEntity() instanceof EnderDragon victim)) return;

		if(victim.getHealth() - event.getDamage() > 0) return;

		event.setCancelled(true);

		Manhunt.getInstance().getGameManager().killDragon(victim);
	}

}
