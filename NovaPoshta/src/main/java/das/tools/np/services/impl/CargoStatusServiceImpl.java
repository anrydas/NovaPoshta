package das.tools.np.services.impl;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.CargoStatus;
import das.tools.np.entity.db.Status;
import das.tools.np.gui.Localized;
import das.tools.np.services.CargoStatusService;
import das.tools.np.services.LocalizeResourcesService;
import jakarta.annotation.PostConstruct;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class CargoStatusServiceImpl implements CargoStatusService, Localized {
    private final LocalizeResourcesService localizeService;
    private final GlyphFont glyphFont;

    static {
        Set<Status> set = ERROR_STATUSES;
        set.add(Status.REMOVED);
        set.add(Status.NOT_FOUND);
        set.add(Status.SENDER_CANCELED);
        set.add(Status.RECEIVER_REJECTED);
        set.add(Status.STORAGE_STOPPING);

        set = NEW_STATUSES;
        set.add(Status.SELF_CREATED);

        set = PROCESSING_STATUSES;
        set.add(Status.IN_CITY);
        set.add(Status.IN_CITY_1);
        set.add(Status.SENDING_TO_CITY);
        set.add(Status.IN_CITY_ADD);
        set.add(Status.COMPLETING);
        set.add(Status.ON_THE_WAY_TO_RECEIVER);
        set.add(Status.ADDRESS_CHANGED);

        set = WAITING_STATUSES;
        set.add(Status.IN_WAREHOUSE);
        set.add(Status.IN_POST_MATE);
        set.add(Status.DELIVERY_FAILED);
        set.add(Status.RECEIVER_CHANGE_DATE);

        set = COMPLETED_STATUSES;
        set.add(Status.COMPLETED);
        set.add(Status.MONEY_WILL_SENT);
        set.add(Status.MONEY_WAS_SENT);
        set.add(Status.BACK_DELIVERY);

        STATUS_COLOR_STRING.put(CargoStatus.NEW, "YELLOWGREEN");
        STATUS_COLOR_STRING.put(CargoStatus.ERROR, "RED");
        STATUS_COLOR_STRING.put(CargoStatus.PROCESSING, "ORANGE");
        STATUS_COLOR_STRING.put(CargoStatus.WAITING, "GREENYELLOW");
        STATUS_COLOR_STRING.put(CargoStatus.COMPLETED, "DARKGREEN");

        STATUS_COLOR.put(CargoStatus.NEW, Color.YELLOWGREEN);
        STATUS_COLOR.put(CargoStatus.ERROR, Color.RED);
        STATUS_COLOR.put(CargoStatus.PROCESSING, Color.ORANGE);
        STATUS_COLOR.put(CargoStatus.WAITING, Color.DARKOLIVEGREEN);
        STATUS_COLOR.put(CargoStatus.COMPLETED, Color.GREEN);

        BIG_STATUS_ICONS.put(CargoStatus.NEW, "/images/number/statuses/newG.png");
        BIG_STATUS_ICONS.put(CargoStatus.ERROR, "/images/number/statuses/error.png");
        BIG_STATUS_ICONS.put(CargoStatus.PROCESSING, "/images/number/statuses/processing.png");
        BIG_STATUS_ICONS.put(CargoStatus.WAITING, "/images/number/statuses/completed.png");
        BIG_STATUS_ICONS.put(CargoStatus.COMPLETED, "/images/number/statuses/completed.png");

        STATUS_ICONS.put(CargoStatus.NEW, "/images/number/statuses/new.png");
        STATUS_ICONS.put(CargoStatus.ERROR, "/images/number/statuses/error.png");
        STATUS_ICONS.put(CargoStatus.PROCESSING, "/images/number/statuses/processing.png");
        STATUS_ICONS.put(CargoStatus.WAITING, "/images/number/statuses/processing.png");
        STATUS_ICONS.put(CargoStatus.COMPLETED, "/images/number/statuses/completed.png");

        STATUS_GLYPHS.put(CargoStatus.NEW, FontAwesome.Glyph.PLUS);
        STATUS_GLYPHS.put(CargoStatus.ERROR, FontAwesome.Glyph.CLOSE);
        STATUS_GLYPHS.put(CargoStatus.PROCESSING, FontAwesome.Glyph.TRUCK);
        STATUS_GLYPHS.put(CargoStatus.WAITING, FontAwesome.Glyph.CART_PLUS);
        STATUS_GLYPHS.put(CargoStatus.COMPLETED, FontAwesome.Glyph.CHECK);
    }

    public CargoStatusServiceImpl(LocalizeResourcesService localizeService, GlyphFont glyphFont) {
        this.localizeService = localizeService;
        this.glyphFont = glyphFont;
    }

    @Override @PostConstruct
    public void initLocale() {
        STATUS_NAMES.put(CargoStatus.NEW, localizeService.getLocalizedResource("number.status.new"));
        STATUS_NAMES.put(CargoStatus.ERROR, localizeService.getLocalizedResource("number.status.error"));
        STATUS_NAMES.put(CargoStatus.PROCESSING, localizeService.getLocalizedResource("number.status.processing"));
        STATUS_NAMES.put(CargoStatus.WAITING, localizeService.getLocalizedResource("number.status.waiting"));
        STATUS_NAMES.put(CargoStatus.COMPLETED, localizeService.getLocalizedResource("number.status.complete"));
    }

    @Override
    public String getStatus(CargoNumber number) {
        return number.getFullData().getStatus();
    }

    @Override
    public CargoStatus getCargoStatus(int statusCode) {
        if (ERROR_STATUSES.contains(Status.valueOf(statusCode))) {
            return CargoStatus.ERROR;
        } else if (NEW_STATUSES.contains(Status.valueOf(statusCode))) {
            return CargoStatus.NEW;
        } else if (PROCESSING_STATUSES.contains(Status.valueOf(statusCode))) {
            return CargoStatus.PROCESSING;
        }  else if (WAITING_STATUSES.contains(Status.valueOf(statusCode))) {
            return CargoStatus.WAITING;
        } else if (COMPLETED_STATUSES.contains(Status.valueOf(statusCode))) {
            return CargoStatus.COMPLETED;
        } else {
            throw new RuntimeException("Wrong cargo status code: " + statusCode);
        }
    }

    @Override
    public String getStatusImage(CargoStatus status) {
        if (STATUS_ICONS.containsKey(status)) {
            return STATUS_ICONS.get(status);
        } else {
            throw new RuntimeException("Wrong cargo status: " + status.name());
        }
    }

    @Override
    public FontAwesome.Glyph getStatusGlyph(CargoStatus status) {
        if (STATUS_GLYPHS.containsKey(status)) {
            return STATUS_GLYPHS.get(status);
        } else {
            throw new RuntimeException("Wrong cargo status: " + status.name());
        }
    }

    @Override
    public String getStatusName(CargoStatus status) {
        if (STATUS_NAMES.containsKey(status)) {
            return STATUS_NAMES.get(status);
        } else {
            throw new RuntimeException("Wrong cargo status: " + status.name());
        }
    }

    @Override
    public CargoStatus fromString(String value) {
        for (Map.Entry<CargoStatus,String> e : STATUS_NAMES.entrySet()) {
            if (e.getValue().equals(value)) return e.getKey();
        }
        throw new RuntimeException("Wrong cargo status value: " + value);
    }

    @Override
    public List<String> getStatusNames() {
        return new ArrayList<>(STATUS_NAMES.values());
    }

    @Override
    public List<CargoStatus> getStatuses() {
        return new ArrayList<>(STATUS_NAMES.keySet());
    }

    @Override
    public String getStatusName(int key) {
        if (Status.contains(key)) {
            return Status.stringValue(Status.valueOf(key));
        }
        throw new RuntimeException("Wrong cargo status key: " + key);
    }

    @Override
    public List<Status> getAllStatusKeys() {
        return new ArrayList<>(Status.getAllKeys());
    }

    @Override
    public int getStatusKeyInt(String value) {
        if (Status.containsString(value)) {
            Status status = Status.fromString(value);
            return status != null ? status.getValue() : 0;
        }
        throw new RuntimeException("Couldn't find status key for value: " + value);
    }

    @Override
    public String getStatusName(Status status) {
        if (status != null && Status.contains(status.getValue())) {
            return Status.stringValue(status);
        }
        throw new RuntimeException("Couldn't find status value for status: " + status);
    }
    @Override
    public Status getStatusFromString(String value) {
        if (Status.containsString(value)) {
            return Status.fromString(value);
        }
        throw new RuntimeException("Wrong status value: " + value);
    }

    @Override
    public List<String> getAllStatusesValues() {
        return new ArrayList<>(Status.getAllValues());
    }

    @Override
    public String getBigStatusImage(CargoStatus status) {
        if (BIG_STATUS_ICONS.containsKey(status)) {
            return BIG_STATUS_ICONS.get(status);
        } else {
            throw new RuntimeException("Wrong cargo status: " + status.name());
        }
    }

    @Override
    public String getStatusTextColor(int statusCode) {
        return STATUS_COLOR_STRING.get(getCargoStatus(statusCode));
    }

    @Override
    public Color getStatusTextColor(CargoStatus status) {
        return STATUS_COLOR.get(status);
    }
}
