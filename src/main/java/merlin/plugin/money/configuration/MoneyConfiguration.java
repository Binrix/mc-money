package merlin.plugin.money.configuration;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class MoneyConfiguration implements ConfigurationSerializable {
    public Float wrongJobPenalty;
    public Float costsForChangingJob;
    public Float costsForApplyingFirstTime;
    public Float baseEfficiency;

    public MoneyConfiguration(final Float wrongJobPenalty, final Float costsForChangingJob, final Float costsForApplyingFirstTime, final Float baseEfficiency) {
        this.wrongJobPenalty = wrongJobPenalty;
        this.costsForChangingJob = costsForChangingJob;
        this.costsForApplyingFirstTime = costsForApplyingFirstTime;
        this.baseEfficiency = baseEfficiency;
    }

    public MoneyConfiguration() {
        this(0.6f, 10_000f, 500f, 1f);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("wrongJobPenalty", wrongJobPenalty);
        data.put("costsForChangingJob", costsForChangingJob);
        data.put("costsForApplyingFirstTime", costsForApplyingFirstTime);
        data.put("baseEfficiency", baseEfficiency);
        return data;
    }

    public static MoneyConfiguration deserialize(Map<String, Object> map) {
        final Float wrongJobPenalty = ((Number) map.get("wrongJobPenalty")).floatValue();
        final Float costsForChangingJob = ((Number) map.get("costsForChangingJob")).floatValue();
        final Float costsForApplyingFirstTime = ((Number) map.get("costsForApplyingFirstTime")).floatValue();
        final Float baseEfficiency = ((Number) map.get("baseEfficiency")).floatValue();

        return new MoneyConfiguration(wrongJobPenalty, costsForChangingJob, costsForApplyingFirstTime, baseEfficiency);
    }
}
