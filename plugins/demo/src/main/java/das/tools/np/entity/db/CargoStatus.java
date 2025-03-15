package das.tools.np.entity.db;

public enum CargoStatus {
    NEW, ERROR, PROCESSING, WAITING, COMPLETED;

    public static CargoStatus valueOf(int value) {
        return CargoStatus.values()[value];
    }
}
