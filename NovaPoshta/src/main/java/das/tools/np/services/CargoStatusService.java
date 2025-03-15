package das.tools.np.services;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.CargoStatus;
import das.tools.np.entity.db.Status;
import das.tools.np.gui.Localized;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.*;

public interface CargoStatusService extends Localized {
    Set<Status> ERROR_STATUSES = new HashSet<>();
    Set<Status> NEW_STATUSES = new HashSet<>();
    Set<Status> PROCESSING_STATUSES = new HashSet<>();
    Set<Status> WAITING_STATUSES = new HashSet<>();
    Set<Status> COMPLETED_STATUSES = new HashSet<>();
    Map<CargoStatus, String> STATUS_COLOR_STRING = new HashMap<>();
    Map<CargoStatus, Color> STATUS_COLOR = new HashMap<>();
    Map<CargoStatus, String> STATUS_ICONS = new HashMap<>();
    Map<CargoStatus, FontAwesome.Glyph> STATUS_GLYPHS = new HashMap<>();
    Map<CargoStatus, String> STATUS_NAMES = new HashMap<>();
    Map<CargoStatus, String> BIG_STATUS_ICONS = new HashMap<>();
    String getStatus(CargoNumber number);

    CargoStatus getCargoStatus(int statusCode);

    String getStatusImage(CargoStatus status);

    FontAwesome.Glyph getStatusGlyph(CargoStatus status);

    String getStatusName(CargoStatus status);

    CargoStatus fromString(String value);

    List<String> getStatusNames();

    List<CargoStatus> getStatuses();

    String getStatusName(int key);

    List<Status> getAllStatusKeys();

    int getStatusKeyInt(String value);

    String getStatusName(Status status);

    Status getStatusFromString(String value);

    List<String> getAllStatusesValues();

    String getBigStatusImage(CargoStatus status);

    String getStatusTextColor(int statusCode);

    Color getStatusTextColor(CargoStatus status);
}
