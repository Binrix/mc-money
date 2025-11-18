package merlin.plugin.money;

import merlin.plugin.money.player.PlayerData;
import org.bukkit.Material;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Helpers {
    public static String formatCoins(final Float coins) {
        return new DecimalFormat("#.##").format(coins);
    }

    public static List<Map.Entry<Material, Float>> sortListOfBlocksByCoins(final Map<Material, Float> blocks) {
        List<Map.Entry<Material, Float>> sortedBlocks = new ArrayList<>(blocks.entrySet());
        sortedBlocks.sort(Map.Entry.comparingByValue((b1, b2) -> {
            if(b1 > b2) {
                return -1;
            } else if(b1 < b2) {
                return 1;
            }

            return 0;
        }));

        return sortedBlocks;
    }

    public static List<Map.Entry<UUID, PlayerData>> sortListOfPlayersByCoins(final Map<UUID, PlayerData> players) {
        List<Map.Entry<UUID, PlayerData>> topPlayers = new ArrayList<>(players.entrySet());
        topPlayers.sort(Map.Entry.comparingByValue((p1, p2) -> {
            final float totalCoins1 = p1.getCoinsInWallet() + p1.getCoinsInAccount();
            final float totalCoins2 = p2.getCoinsInWallet() + p2.getCoinsInAccount();

            if(totalCoins1 > totalCoins2){
                return -1;
            } else if(totalCoins1 < totalCoins2) {
                return 1;
            }

            return 0;
        }));

        return topPlayers.stream().limit(5).collect(Collectors.toList());
    }
}
