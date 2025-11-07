package merlin.plugin.money;

import merlin.plugin.money.commands.CoinsCommand;
import merlin.plugin.money.player.PlayerData;
import merlin.plugin.money.player.Profession;
import merlin.plugin.money.views.BankerView;
import merlin.plugin.money.views.JobSelectionView;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class Money extends JavaPlugin {
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private final Map<Material, Float> blocksToCoins = new HashMap<>();
    private final String playerConfigSection = "player-coins";
    private final String blocksToCoinsSection = "blocks-to-coins";
    private final JobSelectionView jobSelectionView = new JobSelectionView(this);
    private final BankerView bankerView = new BankerView(this);

    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        ConfigurationSerialization.registerClass(PlayerData.class, "PlayerData");

        pm.registerEvents(new EventListeners(this), this);
        pm.registerEvents(jobSelectionView, this);
        pm.registerEvents(bankerView, this);

        PluginCommand coinsCommand = this.getCommand("coins");
        if (coinsCommand != null) {
            coinsCommand.setExecutor(new CoinsCommand(this));
        }

        loadCoinsData();
        loadBlocksToCoins();

        new BukkitRunnable() {
            @Override
            public void run() {
                saveCoinsData();
            }
        }.runTaskTimer(this, 1200L * 5, 1200L * 5);
    }

    public JobSelectionView getJobSelectionView() {
        return jobSelectionView;
    }
    public BankerView getBankerView() {
        return bankerView;
    }

    @Override
    public void onDisable() {
        saveCoinsData();
    }

    private void loadBlocksToCoins() {
        ConfigurationSection configSection = getConfig().getConfigurationSection(blocksToCoinsSection);

        if (configSection == null) {
            getLogger().log(Level.WARNING, "No blocks to coins provided.");
            return;
        }

        for (String material : configSection.getKeys(false)) {
            try {
                Object coinsInConfig = configSection.get(material);

                if (coinsInConfig == null) {
                    continue;
                }

                Float coins = Float.parseFloat(coinsInConfig.toString());
                blocksToCoins.put(Material.valueOf(material), coins);
            } catch (Exception exception) {
                getLogger().log(Level.SEVERE, "Could not parse value of " + material);
            }
        }

        getLogger().log(Level.FINE, "Loaded " + blocksToCoins.size() + " blocks.");
    }

    public Map<Material, Float> getBlocksToCoins() {
        return blocksToCoins;
    }

    public PlayerData getPlayerData(final Player player) {
        return players.getOrDefault(player.getUniqueId(), new PlayerData());
    }

    public void addPlayerCoins(final Player player, final Float coins) {
        PlayerData playerData = getPlayerData(player);
        playerData.addCoins(coins);

        players.put(player.getUniqueId(), playerData);
    }

    public boolean hasPlayerProfession(final Player player, final Profession profession) {
        return getPlayerData(player).hasProfession(profession);
    }

    public void addNewBlockToCoins(final Material material, final Float value) {
        blocksToCoins.put(material, value);
        saveBlocksToCoinsData();
    }

    public Map<UUID, PlayerData> getPlayersCoins() {
        return players;
    }

    public boolean isPlayerInList(UUID playerUuid) {
        return players.containsKey(playerUuid);
    }

    private void saveBlocksToCoinsData() {
        if (getConfig().contains(blocksToCoinsSection)) {
            getConfig().set(blocksToCoinsSection, null);
        }

        ConfigurationSection configurationSection = getConfig().createSection(blocksToCoinsSection);

        for(Map.Entry<Material, Float> entry : blocksToCoins.entrySet()) {
            configurationSection.set(entry.getKey().toString(), entry.getValue());
        }
        saveConfig();
    }

    private void saveCoinsData () {
        if (getConfig().contains(playerConfigSection)) {
            getConfig().set(playerConfigSection, null);
        }

        ConfigurationSection configSection = getConfig().createSection(playerConfigSection);

        for (Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
            configSection.set(entry.getKey().toString(), entry.getValue());
        }
        saveConfig();
    }

    private void loadCoinsData () {
        if (!getConfig().contains(playerConfigSection)) {
            return;
        }

        ConfigurationSection configSection = getConfig().getConfigurationSection(playerConfigSection);

        if (configSection != null) {
            for (String uuidString : configSection.getKeys(false)) {
                PlayerData playerData = configSection.getObject(uuidString, PlayerData.class);
                UUID uuid = UUID.fromString(uuidString);
                players.put(uuid, playerData);
            }
        }
    }
}
