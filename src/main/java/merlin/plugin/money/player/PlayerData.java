package merlin.plugin.money.player;

import merlin.plugin.money.SetMethodReturn;
import merlin.plugin.money.SetResult;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class PlayerData implements ConfigurationSerializable, IPlayerData {
    private Float coinsWallet;
    private Float coinsAccount;
    private Profession profession;

    public PlayerData(final Float coinsWallet, final Float coinsAccount, final Profession profession) {
        this.coinsWallet = coinsWallet;
        this.coinsAccount = coinsAccount;
        this.profession = profession;
    }

    public PlayerData() {
        this(0f, 0f, Profession.NONE);
    }


    public Profession getProfession() {
        return profession;
    }

    public boolean removeCoins(final Float coinsToRemove) {
        if(coinsWallet < coinsToRemove) {
            return false;
        }

        coinsWallet -= coinsToRemove;
        return true;
    }

    public void addCoins(final Float coinsToAdd) {
        coinsWallet += coinsToAdd;
    }
    public void loseCoinsInWallet() {
        coinsWallet = 0f;
    }

    public SetMethodReturn setProfession(final Profession newProfession) {
        float costsForProfessionChange = 0f;

        if (profession == Profession.NONE) {
            costsForProfessionChange = 500f;
        } else {
            if(profession == newProfession) {
                return new SetMethodReturn(ChatColor.DARK_RED + "You already have the profession.", SetResult.FAILURE);
            }
            costsForProfessionChange = 10_000f;
        }

        final Profession oldProfession = profession;
        if(removeCoins(costsForProfessionChange)) {
            profession = newProfession;
            return new SetMethodReturn("You updated your profession from " + ChatColor.DARK_AQUA + oldProfession + ChatColor.WHITE + " to " + ChatColor.DARK_AQUA + profession, SetResult.SUCCESS);
        } else {
            return new SetMethodReturn("You don't have enough Coins to change your profession " + ChatColor.DARK_AQUA + oldProfession + ChatColor.WHITE + " to " + ChatColor.DARK_AQUA + newProfession, SetResult.FAILURE);
        }

    }

    public boolean depositCoins(final Float coinsToDeposit) {
        if(coinsToDeposit > coinsWallet) {
            coinsAccount += coinsWallet;
            coinsWallet = 0f;
        } else {
            coinsAccount += coinsToDeposit;
            coinsWallet -= coinsToDeposit;
        }

        return true;
    }

    public boolean withdraw(final Float coinsToWithdraw) {
        if(coinsToWithdraw > coinsAccount) {
            coinsWallet += coinsAccount;
            coinsAccount = 0f;
        } else {
            coinsWallet = coinsToWithdraw;
            coinsAccount -= coinsToWithdraw;
        }

        return true;
    }

    public Float getCoinsWallet() {
        return coinsWallet;
    }
    public Float getCoinsAccount() {
        return coinsAccount;
    }

    public boolean hasProfession(final Profession profession) {
        return this.profession == profession;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("coinsWallet", coinsWallet);
        data.put("coinsAccount", coinsAccount);
        data.put("profession", profession.name());
        return data;
    }

    public static PlayerData deserialize(Map<String, Object> map) {
        final Float coinsWallet = ((Number) map.get("coinsWallet")).floatValue();
        final Float coinsAccount = ((Number) map.get("coinsAccount")).floatValue();
        Profession profession = Profession.valueOf((String) map.get("profession"));

        return new PlayerData(coinsWallet, coinsAccount, profession);
    }
}
