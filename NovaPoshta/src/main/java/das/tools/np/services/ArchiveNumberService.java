package das.tools.np.services;

public interface ArchiveNumberService {
    void moveNumberToArchive(String number);

    void restoreNumber(String number);
}
