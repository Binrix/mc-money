package merlin.plugin.money;

import merlin.plugin.money.player.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerDataFile {
    public static final String playerDataFileName = "players.yml";

    public static Map<UUID, PlayerData> loadPlayerData(Money plugin) {
        try {
            final File playerDataFile = open(plugin);

            FileConfiguration configuration = YamlConfiguration.loadConfiguration(playerDataFile);

            final Map<UUID, PlayerData> loadedPlayers = new HashMap<>();

            for (String uuidString : configuration.getKeys(false)) {
                PlayerData playerData = configuration.getObject(uuidString, PlayerData.class);
                UUID uuid = UUID.fromString(uuidString);
                loadedPlayers.put(uuid, playerData);
            }

            return loadedPlayers;
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not create player data file " + playerDataFileName);
        }

        return Collections.emptyMap();
    }

    private static File open(Money plugin) {
        final File playerDataFile = new File(plugin.getDataFolder(), playerDataFileName);

        try {
            if(playerDataFile.createNewFile()) {
                plugin.getLogger().log(Level.INFO, "File " + playerDataFile + " was created.");
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not create player data file " + playerDataFile);
        }

        return playerDataFile;
    }

    public static void savePlayerData(Money plugin, Map<UUID, PlayerData> players) {
        try {
            final File playerDataFile = open(plugin);
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(playerDataFile);

            for (Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
                configuration.set(entry.getKey().toString(), entry.getValue());
            }

            configuration.save(playerDataFile);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player data.");
        }
    }
}
