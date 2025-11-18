package merlin.plugin.money.eventlisteners;

import merlin.plugin.money.Money;
import merlin.plugin.money.player.PlayerData;
import merlin.plugin.money.player.Profession;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

public class HuntingEventsListener implements Listener {
    private final Money plugin;
    private final Float baseEfficiency;
    private final Float wrongJobPenalty;
    private final Map<EntityType, Float> entities;

    public HuntingEventsListener(Money plugin, Map<EntityType, Float> entities, final Float wrongJobPenalty, final Float baseEfficiency) {
        this.plugin = plugin;
        this.entities = entities;
        this.baseEfficiency = baseEfficiency;
        this.wrongJobPenalty = wrongJobPenalty;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        EntityDamageEvent lastDamageEvent = entity.getLastDamageCause();

        if(lastDamageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) lastDamageEvent;
            Entity killerEntity = damageByEntityEvent.getDamager();

            if(killerEntity instanceof Player) {
                Player killer = (Player)killerEntity;
                float coins = 0f;

                PlayerData player = plugin.getPlayerData(killer);

                if(entities.containsKey(entity.getType())) {
                    coins = entities.get(entity.getType()) * (player.hasProfession(Profession.HUNTER) ? baseEfficiency : wrongJobPenalty);
                }

                player.addCoins(coins);
            }
        }
    }
}
