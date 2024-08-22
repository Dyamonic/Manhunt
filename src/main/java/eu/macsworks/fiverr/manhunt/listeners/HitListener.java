package eu.macsworks.fiverr.manhunt.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HitListener implements Listener {

	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		event.setCancelled(event.getEntity().hasMetadata("no-hit"));
	}

}
