package eu.macsworks.fiverr.manhunt.managers;

import eu.macsworks.fiverr.manhunt.Manhunt;
import eu.macsworks.fiverr.manhunt.utils.PluginLoader;
import eu.macsworks.premium.macslibs.utils.InventoryBuilder;
import eu.macsworks.premium.macslibs.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

	//K = Player UUID
	//V = True: Runner, False: Chaser
	private final Map<Player, Boolean> players = new HashMap<>();
	private long timer = 0;

	private final Map<Block, BlockData> blocks = new HashMap<>();

	private final Map<Player, Player> tracking = new HashMap<>();

	private final PluginLoader loader;
	@Getter private final World world;

	@Getter private GameState state = GameState.WAITING;

	private EnderDragon dragon;

	public GameManager(){
		loader = Manhunt.getInstance().getMacsPluginLoader();
		world = Bukkit.getWorld(loader.getGameWorld());
	}

	public void tick(){
		switch (state){
			case WAITING -> {
				if(players.size() < loader.getMinPlayers()){
					timer = 60;
					return;
				}

				timer--;
				if(timer % 5 == 0){
					broadcastToPlayers(loader.getLang("starting").replace("%timer%", String.valueOf(timer)));
				}

				if(timer <= 0){
					startGame();
				}
			}

			case PLAYING -> {
				timer++;

				tracking.keySet().removeIf(Objects::isNull);
				tracking.values().removeIf(Objects::isNull);
				players.keySet().removeIf(Objects::isNull);

				tracking.forEach((tracker, tracked) -> tracker.setCompassTarget(tracked.getLocation() ));

				if(timer == 30){
					getPlayers(false).forEach(p -> {
						p.teleport(world.getSpawnLocation());
						p.removeMetadata("no-hit", Manhunt.getInstance());

						p.getInventory().addItem(new ItemStack(Material.GOLDEN_SWORD));
						p.getInventory().addItem(ItemBuilder.builder()
										.material(Material.COMPASS)
										.name(loader.getLang("tracker"))
										.interactable(phisicalInteractResult -> {
											HashMap<Integer, ItemStack> items = new HashMap<>();

											int[] i = new int[]{0};
											getPlayers(true).forEach(p1 -> {
												items.put(i[0], ItemBuilder.builder()
																.skullOf(p1)
																.name(loader.getLang("track").replace("%player%", p1.getName()))
																.interactive(interactResult -> {
																	tracking.put(interactResult.getClicker(), p1);
																})
														.build());
											});

											phisicalInteractResult.getClicker().openInventory(InventoryBuilder.builder()
															.slots(54)
															.setItems(items)
															.filled(ItemBuilder.builder().material(Material.BLACK_STAINED_GLASS_PANE).name("§f ").makeStatic().build())
													.build());
										})
								.build());

						p.getInventory().setItem(8, new ItemStack(Material.APPLE, 16));

						p.getInventory().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
					});

					broadcastToPlayers(loader.getLang("chasers-released"));
				}

				if(players.isEmpty()){
					state = GameState.CLEANUP;
					return;
				}

				if(players.values().stream().distinct().count() == 1 && players.values().stream().findFirst().get()){
					Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(loader.getLang("won").replace("%team%", loader.getLang("chaser"))));

					Bukkit.getScheduler().scheduleSyncDelayedTask(Manhunt.getInstance(), () -> {
						Bukkit.getOnlinePlayers().forEach(p -> p.kickPlayer(loader.getLang("restarting")));

						state = GameState.CLEANUP;
					}, 20L * 5);
				}
			}

			case CLEANUP -> {
				blocks.forEach((b, data) -> b.setBlockData(data, false));
				dragon.setInvisible(false);

				timer = 0;
				players.clear();
				tracking.clear();
				state = GameState.WAITING;
			}
		}
	}

	public void startGame(){
		state = GameState.PLAYING;

		getPlayers().forEach(p -> {
			broadcastToPlayers(loader.getLang("role")
					.replace("%role%", isRunner(p) ? "runner" : "chaser")
					.replace("%role_rules%", isRunner(p) ? "runner-rules" : "chaser-rules"));
		});

		getPlayers(true).forEach(p -> {
			p.teleport(world.getSpawnLocation());

			p.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
			p.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
			p.getInventory().addItem(new ItemStack(Material.WOODEN_SHOVEL));
			p.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));

			p.getInventory().setItem(8, new ItemStack(Material.APPLE, 16));

			p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		});

		timer = 0;
	}

	public void killPlayer(Player player){
		players.remove(player);

		broadcastToPlayers(loader.getLang("killed").replace("%player%", player.getName()));
	}

	public void killDragon(EnderDragon dragon){
		this.dragon = dragon;

		dragon.setInvisible(true);
		dragon.setHealth(dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());

		Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(loader.getLang("won").replace("%team%", loader.getLang("runner"))));

		Bukkit.getScheduler().scheduleSyncDelayedTask(Manhunt.getInstance(), () -> {
			Bukkit.getOnlinePlayers().forEach(p -> p.kickPlayer(loader.getLang("restarting")));

			state = GameState.CLEANUP;
		}, 20L * 5);
	}

	public void addPlayer(Player player){
		boolean isRunner = players.values().stream().distinct().filter(b -> b).count() < players.values().stream().distinct().filter(b -> !b).count();

		players.put(player, isRunner);
		player.teleport(Manhunt.getInstance().getMacsPluginLoader().getLobbyLocation());
	}

	public void setDataToRestore(Block block, BlockData data){
		//If players have broken a placed block
		if(blocks.containsKey(block) && blocks.get(block).getMaterial() == Material.AIR){
			blocks.remove(block);
			return;
		}

		blocks.put(block, data.clone());
	}

	public void respawnChaser(Player player){

	}

	public boolean isRunner(Player player){
		return players.get(player);
	}

	public List<Player> getPlayers(){
		return new ArrayList<>(players.keySet());
	}

	public List<Player> getPlayers(boolean runners){
		return players.keySet().stream().filter(p -> isRunner(p) == runners).collect(Collectors.toList());
	}

	public void remPlayer(Player player){

	}

	public void win(boolean runners){

	}

	public void broadcastToPlayers(String message){
		players.keySet().forEach(p -> p.sendMessage(message));
	}
}
