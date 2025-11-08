package merlin.plugin.money;

public class SetMethodReturn {
    public final String message;
    public final SetResult result;

    public SetMethodReturn(final String message, final SetResult result) {
        this.message = message;
        this.result = result;
    }
}
