package merlin.plugin.money;

import merlin.plugin.money.commands.CoinsCommand;
import merlin.plugin.money.configuration.MoneyConfiguration;
import merlin.plugin.money.player.PlayerData;
import merlin.plugin.money.player.Profession;
import merlin.plugin.money.views.BankerView;
import merlin.plugin.money.views.JobSelectionView;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class Money extends JavaPlugin {
    private Map<UUID, PlayerData> players = new HashMap<>();
    private final Map<Material, Float> blocksToCoins = new HashMap<>();
    private final Map<EntityType, Float> entities = new HashMap<>();
    private final String blocksSection = "blocks";
    private final String entitySection = "mobs";
    private final String configurationSection = "config";
    private MoneyConfiguration moneyConfiguration = new MoneyConfiguration();
    private final JobSelectionView jobSelectionView = new JobSelectionView(this);
    private final BankerView bankerView = new BankerView(this);

    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        ConfigurationSerialization.registerClass(PlayerData.class, "PlayerData");
        ConfigurationSerialization.registerClass(MoneyConfiguration.class, "Configuration");

        loadConfiguration();
        loadBlocksToCoins();
        loadEntities();

        pm.registerEvents(new EventListeners(this), this);
        pm.registerEvents(jobSelectionView, this);
        pm.registerEvents(bankerView, this);

        PluginCommand coinsCommand = this.getCommand("coins");
        if (coinsCommand != null) {
            coinsCommand.setExecutor(new CoinsCommand(this));
        }

        players = PlayerDataFile.loadPlayerData(this);

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

    public MoneyConfiguration getMoneyConfiguration() {
        return moneyConfiguration;
    }

    @Override
    public void onDisable() {
        saveCoinsData();
    }

    private void loadBlocksToCoins() {
        ConfigurationSection configSection = getConfig().getConfigurationSection(blocksSection);

        if (configSection == null) {
            getLogger().log(Level.WARNING, "No blocks have been configured.");
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

    private void loadEntities() {
        ConfigurationSection configSection = getConfig().getConfigurationSection(entitySection);

        if(configSection == null) {
            getLogger().log(Level.WARNING, "No mobs have been configured.");
            return;
        }

        for(String entity : configSection.getKeys(false)) {
            try {
                Object entityCoinsInConfig = configSection.get(entity);

                if(entityCoinsInConfig == null) {
                    continue;
                }

                Float coins = Float.parseFloat(entityCoinsInConfig.toString());
                entities.put(EntityType.valueOf(entity), coins);
            } catch (Exception exception) {
                getLogger().log(Level.SEVERE, "Could not parse value of " + entity);
            }
        }
    }

    public Map<Material, Float> getBlocks() {
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

    public void addNewEntity(final EntityType type, final Float value) {
        entities.put(type, value);

        getLogger().log(Level.INFO, "Add entity of type " + ChatColor.DARK_AQUA + type + ChatColor.WHITE + " with " + ChatColor.GOLD + value + " Coins.");
        saveEntityCoinsData();
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

    private void loadConfiguration() {
        if(getConfig().contains(configurationSection)) {
            moneyConfiguration = getConfig().getObject(configurationSection, MoneyConfiguration.class);
            getLogger().log(Level.INFO, "Configuration was loaded.");
        } else {
            getLogger().log(Level.INFO, "Configuration didn't exist, created defaults...");
            moneyConfiguration = new MoneyConfiguration();
        }
    }

    private void saveConfiguration() {
        getConfig().set(configurationSection, moneyConfiguration);
        saveConfig();
    }

    private void saveBlocksToCoinsData() {
        if (getConfig().contains(blocksSection)) {
            getConfig().set(blocksSection, null);
        }

        ConfigurationSection configurationSection = getConfig().createSection(blocksSection);

        for(Map.Entry<Material, Float> entry : blocksToCoins.entrySet()) {
            configurationSection.set(entry.getKey().toString(), entry.getValue());
        }
        saveConfig();
    }

    private void saveEntityCoinsData() {
        if(getConfig().contains(entitySection)) {
            getConfig().set(entitySection, null);
        }

        ConfigurationSection configSection = getConfig().createSection(entitySection);

        for(Map.Entry<EntityType, Float> entry : entities.entrySet()) {
            configSection.set(entry.getKey().toString(), entry.getValue());
        }
        saveConfig();

        getLogger().log(Level.INFO, "Saved " + );
    }

    private void saveCoinsData () {
        PlayerDataFile.savePlayerData(this, players);
        saveConfiguration();
    }
}
