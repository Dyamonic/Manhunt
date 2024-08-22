package eu.macsworks.fiverr.manhunt.listeners;

import eu.macsworks.fiverr.manhunt.Manhunt;
import eu.macsworks.fiverr.manhunt.managers.GameState;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ModifyListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(Manhunt.getInstance().getGameManager().getState() != GameState.PLAYING && !event.getPlayer().hasPermission("manhunt.admin")){
			event.setCancelled(true);
			return;
		}

		Manhunt.getInstance().getGameManager().setDataToRestore(event.getBlockPlaced(), Material.AIR.createBlockData());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(Manhunt.getInstance().getGameManager().getState() != GameState.PLAYING && !event.getPlayer().hasPermission("manhunt.admin")){
			event.setCancelled(true);
			return;
		}

		Manhunt.getInstance().getGameManager().setDataToRestore(event.getBlock(), event.getBlock().getBlockData());
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		if(Manhunt.getInstance().getGameManager().getState() != GameState.PLAYING){
			event.setCancelled(true);
			return;
		}

		event.blockList().forEach(block -> {
			Manhunt.getInstance().getGameManager().setDataToRestore(block, block.getBlockData());
		});
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if(Manhunt.getInstance().getGameManager().getState() != GameState.PLAYING){
			event.setCancelled(true);
			return;
		}

		event.blockList().forEach(block -> {
			Manhunt.getInstance().getGameManager().setDataToRestore(block, block.getBlockData());
		});
	}

}
