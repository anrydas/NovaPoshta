package das.tools.np.entity.db;

public enum NumberType {
    UNDEF, IN, OUT;
    public static NumberType valueOf(int value) {
        return NumberType.values()[value];
    }
}
