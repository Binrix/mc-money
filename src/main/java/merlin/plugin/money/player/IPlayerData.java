package merlin.plugin.money.player;

import merlin.plugin.money.SetMethodReturn;

/**
 * Holds the information about the player and provides useful functions.
 */
public interface IPlayerData {
    /**
     * Removes an amount of coins from the player.
     * @param coinsToRemove The amount of coins to be removed.
     * @return True on success, otherwise false.
     */
    boolean removeCoins(final Float coinsToRemove);

    /**
     * Adds an amount of coins to the player.
     * @param coinsToAdd The amount of coins to be added.
     */
    void addCoins(final Float coinsToAdd);

    /**
     * Removes the coins from the players wallet.
     */
    void loseCoinsInWallet();

    /**
     * Gets the current profession of the player.
     * @return The profession of the player.
     */
    Profession getProfession();

    /**
     * Sets a profession for the player.
     * @param newProfession The new profession to be set.
     * @return Return object with message and result, if it was a success or a failure.
     */
    SetMethodReturn setProfession(final Profession newProfession);

    /**
     * Adds an amount of coins to the account and removes it from the wallet.
     * @param coinsToDeposit The amount of coins to be deposited.
     * @return True on success, otherwise false.
     */
    boolean depositCoins(final Float coinsToDeposit);

    /**
     * Removes an amount of coins from the account and adds it to the wallet of the player.
     * @param coinsToWithdraw The coins to be withdrawn.
     * @return True on success, otherwise false.
     */
    boolean withdraw(final Float coinsToWithdraw);

    /**
     * Returns the amount of coins in the wallet of the player.
     * @return The amount of coins in the wallet.
     */
    Float getCoinsInWallet();

    /**
     * Returns the amount of coins in the account of the player.
     * @return The amount of coins in the account.
     */
    Float getCoinsInAccount();

    /**
     * Evaluates if the player has the given profession.
     * @param profession The profession to validate.
     * @return True if the player has the profession, otherwise false.
     */
    boolean hasProfession(final Profession profession);
}
