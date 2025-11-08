package merlin.plugin.money.handlers;

import merlin.plugin.money.Money;
import merlin.plugin.money.npcs.NPCType;
import merlin.plugin.money.player.PlayerData;
import merlin.plugin.money.player.Profession;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CommandHandler {
    private final static String errorMessage = "Something went wrong executing the command.";

    public static boolean showScoreboard(final Player player, final Money plugin, final String[] args) {
        try {
            Map<UUID, PlayerData> players = plugin.getPlayersCoins();

            if (players.isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "No coins have been collected yet.");
                return true;
            }

            printListOfPlayersToChat(player, sortListOfPlayersByCoins(players));

            return true;
        } catch (Exception exception) {
            player.sendMessage(ChatColor.DARK_RED + errorMessage);
            plugin.getLogger().log(Level.WARNING, "Something went wrong: " + exception.getMessage());
            return false;
        }
    }

    public static boolean createNPC(final Player player, final Money plugin, final String[] args) {
        try {
            if(!player.isOp()) {
                player.sendMessage( ChatColor.DARK_RED + "You don't have permissions to use this command.");
                return true;
            }

            final Location location = player.getLocation();
            final NPCType typeOfNPC = NPCType.valueOf(args[0].toUpperCase());
            final String nameOfNPC = args[1];

            World world = location.getWorld();
            if(world == null) return true;

            createNPC(world, nameOfNPC, typeOfNPC, location, plugin);
        } catch(Exception exception) {
            player.sendMessage(ChatColor.DARK_RED + "Something went wrong creating the NPC. Check console for details");
            plugin.getLogger().log(Level.WARNING, "Error for creating a NPC: " + exception.getMessage());
            return false;
        }

        return true;
    }

    public static boolean showPlayerBalance(final Player player, final Money money, final String[] args) {
        try {
            final Float coinsInWallet = money.getPlayerData(player).getCoinsWallet();
            final Float coinsInAccount = money.getPlayerData(player).getCoinsAccount();
            player.sendMessage("In your wallet: " + ChatColor.GOLD + coinsInWallet + " Coins." + ChatColor.WHITE + "\nIn your account: " + ChatColor.GOLD + coinsInAccount + " Coins.");

            return true;
        } catch (Exception exception) {
            player.sendMessage(ChatColor.DARK_RED + errorMessage);
            return false;
        }
    }

    public static boolean addBlockToValue(final Player player, final Money plugin, final String[] args) {
        try {
            if(!player.isOp()) {
                player.sendMessage( ChatColor.DARK_RED + "You don't have permissions to use this command.");
                return true;
            }

            final Material material = Material.getMaterial(args[0]);
            final Float value = Float.valueOf(args[1]);

            if (material == null) {
                player.sendMessage(ChatColor.RED + "The material doesn't exist.");
                return true;
            }

            plugin.addNewBlockToCoins(material, value);
            player.sendMessage("Block " + ChatColor.DARK_AQUA + material.name() + ChatColor.WHITE + " with " + ChatColor.GOLD + value + ChatColor.WHITE + " was added.");
        } catch (Exception exception) {
            player.sendMessage(ChatColor.RED + "An error occurred adding the block.");
            return false;
        }

        return true;
    }

    public static boolean showBlockValues(final Player player, final Money plugin, final String[] args) {
        try {
            final Map<Material, Float> blocksToCoins = plugin.getBlocksToCoins();

            if(blocksToCoins.isEmpty()) {
                player.sendMessage("No blocks are configured.");
                return true;
            }

            for(Map.Entry<Material, Float> block : blocksToCoins.entrySet()) {
                player.sendMessage("Block: " + ChatColor.DARK_AQUA + block.getKey().name() + ChatColor.WHITE + ", value: " + ChatColor.GOLD + block.getValue() + " Coins");
            }

            return true;
        } catch (Exception exception) {
            player.sendMessage(ChatColor.DARK_RED + errorMessage);
            return false;
        }
    }

    public static boolean showPlayerProfession(final Player player, final Money plugin, final String[] args) {
        Profession profession = plugin.getPlayerData(player).getProfession();
        player.sendMessage("Your profession is " + ChatColor.DARK_AQUA + profession.name());

        return true;
    }

    public static List<String> getAutoCompletion(String[] args) {
        List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            completions.add("balance");
            completions.add("scoreboard");
            completions.add("job");
            completions.add("blocks");
            completions.add("npc");
        } else if(args.length == 2 && args[0].equalsIgnoreCase("job")) {
            for(Profession job : Profession.values()) {
                completions.add(job.name().toLowerCase());
            }
        } else if(args.length == 2 && (args[0].equalsIgnoreCase("blocks") || args[0].equalsIgnoreCase("npc"))) {
            completions.add("add");
        } else if(args.length == 3 && args[1].equalsIgnoreCase("add")) {
            for(NPCType type : NPCType.values()) {
                completions.add(type.name().toLowerCase());
            }
        }

        return completions;
    }

    private static List<Map.Entry<UUID, PlayerData>> sortListOfPlayersByCoins(final Map<UUID, PlayerData> players) {
        List<Map.Entry<UUID, PlayerData>> topPlayers = new ArrayList<>(players.entrySet());
        topPlayers.sort(Map.Entry.comparingByValue((o1, o2) -> {
            final float totalCoins1 = o1.getCoinsWallet() + o1.getCoinsAccount();
            final float totalCoins2 = o2.getCoinsWallet() + o2.getCoinsAccount();

            if(totalCoins1 > totalCoins2){
                return 1;
            } else if(totalCoins1 < totalCoins2) {
                return -1;
            }

            return 0;
        }));

        return topPlayers.stream().limit(5).collect(Collectors.toList());
    }

    private static void printListOfPlayersToChat(final Player player, final List<Map.Entry<UUID, PlayerData>> topPlayers) {
        player.sendMessage(ChatColor.GOLD + "--- Coins Scoreboard ---");
        int index = 1;
        for (Map.Entry<UUID, PlayerData> entry : topPlayers) {
            UUID playerUuid = entry.getKey();
            PlayerData playerData = entry.getValue();
            float totalCoins = playerData.getCoinsWallet() + playerData.getCoinsAccount();

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);
            String playerName = (offlinePlayer.getName() != null) ? offlinePlayer.getName() : "Unknown (" + playerUuid.toString().substring(0, 4) + "...)";

            player.sendMessage(ChatColor.GREEN + String.valueOf(index) + ". " + playerName + ": " + ChatColor.GOLD + totalCoins + " Coins");
            index++;
        }
        player.sendMessage(ChatColor.GOLD + "---------------------");
    }

    private static void createNPC(final World world, final String name, NPCType type, Location location, final Money plugin) {
        Villager npc = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
        npc.setVillagerType(Villager.Type.JUNGLE);
        npc.setCustomName(name);
        npc.setProfession(Villager.Profession.NONE);
        npc.setCustomNameVisible(true);
        npc.setAI(false);
        npc.setSilent(true);
        npc.setInvulnerable(true);
        npc.setCollidable(false);
        npc.setGravity(false);
        npc.setVillagerLevel(1);

        NamespacedKey key = new NamespacedKey(plugin, "is_npc");
        npc.getPersistentDataContainer().set(key, PersistentDataType.STRING, type.name());
    }
}
