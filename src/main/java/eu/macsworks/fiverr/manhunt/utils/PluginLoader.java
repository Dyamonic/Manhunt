package eu.macsworks.fiverr.manhunt.utils;

import eu.macsworks.fiverr.manhunt.Manhunt;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PluginLoader {

	@Getter(AccessLevel.NONE)
	private final Map<String, String> lang = new HashMap<>();

	private int minPlayers, maxPlayers;

	private String gameWorld;

	@Setter private Location lobbyLocation;

	public void load() {
		File configFile = new File(Manhunt.getInstance().getDataFolder() + "/config.yml");
		if (!configFile.exists()) Manhunt.getInstance().saveResource("config.yml", false);

		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		config.getConfigurationSection("lang").getKeys(false).forEach(s -> lang.put(s, ColorTranslator.translate(config.getString("lang." + s))));

		minPlayers = config.getInt("min-players");
		maxPlayers = config.getInt("max-players");

		gameWorld = config.getString("game-world");

		File storageFile = new File(Manhunt.getInstance().getDataFolder() + "/storage.yml");
		if (!storageFile.exists()) return;
		YamlConfiguration storage = YamlConfiguration.loadConfiguration(storageFile);

		lobbyLocation = storage.getLocation("lobby-location");
	}

	public String getLang(String key) {
		if (!lang.containsKey(key)) return key + " not present in config.yml. Add it under lang!";
		return lang.get(key);
	}

	public void save() {
		YamlConfiguration storage = new YamlConfiguration();

		storage.set("lobby-location", lobbyLocation);

		try {
			storage.save(new File(Manhunt.getInstance().getDataFolder() + "/storage.yml"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}