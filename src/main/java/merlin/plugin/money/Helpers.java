package merlin.plugin.money;

import java.text.DecimalFormat;

public class Helpers {
    public static String formatCoins(final Float coins) {
        return new DecimalFormat("#.##").format(coins);
    }
}
