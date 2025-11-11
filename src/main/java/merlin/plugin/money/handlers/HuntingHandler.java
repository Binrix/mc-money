package merlin.plugin.money.handlers;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class HuntingHandler {
    private final Map<EntityType, Float> entities;
    private final Float wrongJobPenalty;
    private final Float baseEfficiency;

    public HuntingHandler(final Map<EntityType, Float> entities, final Float wrongJobPenalty, final Float baseEfficiency) {
        this.entities = entities;
        this.wrongJobPenalty = wrongJobPenalty;
        this.baseEfficiency = baseEfficiency;
    }

    public Float handleHunting(final boolean hasPlayerProfession, final LivingEntity entity) {
        EntityType type = entity.getType();

        System.out.println("type: " + type);

        for(Map.Entry<EntityType, Float> f : entities.entrySet() ) {
            System.out.println("entity type: " + f.getKey() + ", value: " + f.getValue());
        }

        if(entities.containsKey(entity.getType())) {
            return entities.get(entity.getType()) * (hasPlayerProfession ? baseEfficiency : wrongJobPenalty);
        }

        return 0f;
    }
}
