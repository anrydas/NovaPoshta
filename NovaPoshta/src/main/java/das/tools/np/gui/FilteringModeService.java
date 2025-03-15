package das.tools.np.gui;

import das.tools.np.gui.enums.FilteringMode;
import das.tools.np.services.LocalizeResourcesService;
import jakarta.annotation.PostConstruct;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FilteringModeService implements Localized {
    public static final Map<FilteringMode,String> FILTER_MODES_NAMES = new HashMap<>();
    private final LocalizeResourcesService localizeService;
    private FilteringMode currentMode;

    public FilteringModeService(LocalizeResourcesService localizeService) {
        this.localizeService = localizeService;
    }

    @PostConstruct
    public void initialize() {
        initLocale();
        currentMode = FilteringMode.ALL;
    }

    @Override
    public void initLocale() {
        FILTER_MODES_NAMES.put(FilteringMode.ALL, localizeService.getLocalizedResource("fm.all"));
        FILTER_MODES_NAMES.put(FilteringMode.UNCOMPLETED, localizeService.getLocalizedResource("fm.uncompleted"));
        FILTER_MODES_NAMES.put(FilteringMode.INBOUND, localizeService.getLocalizedResource("fm.inbound"));
        FILTER_MODES_NAMES.put(FilteringMode.OUTBOUND, localizeService.getLocalizedResource("fm.outbound"));
    }

    public String getModeName(FilteringMode mode) {
        return FILTER_MODES_NAMES.get(mode);
    }

    public FilteringMode getModeValue(String value) {
        for (Map.Entry<FilteringMode, String> entry: FILTER_MODES_NAMES.entrySet()) {
            if (entry.getValue().equals(value)) return entry.getKey();
        }
        throw new RuntimeException("Unknown Filtering Mode");
    }

    public void setAllModes(ObservableList<FilteringMode> list) {
        if (list.size() > 0) list.clear();
        list.addAll(FILTER_MODES_NAMES.keySet().stream().sorted().toList());
    }

    public FilteringMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(FilteringMode currentMode) {
        this.currentMode = currentMode;
    }
}
