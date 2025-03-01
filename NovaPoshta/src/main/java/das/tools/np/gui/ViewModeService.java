package das.tools.np.gui;

import das.tools.np.gui.enums.ViewMode;
import das.tools.np.services.LocalizeResourcesService;
import jakarta.annotation.PostConstruct;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ViewModeService implements Localized {
    public static final Map<ViewMode,String> VIEW_MODES_NAMES = new HashMap<>();
    private final LocalizeResourcesService localizeService;

    private ViewMode currentMode;

    public ViewModeService(LocalizeResourcesService localizeService) {
        this.localizeService = localizeService;
    }

    @PostConstruct
    public void initialize() {
        initLocale();
        currentMode = ViewMode.VM_NUMBERS;
    }

    @Override
    public void initLocale() {
        VIEW_MODES_NAMES.put(ViewMode.VM_NUMBERS, localizeService.getLocalizedResource("vm.numbers"));
        VIEW_MODES_NAMES.put(ViewMode.VM_GROUPS, localizeService.getLocalizedResource("vm.groups"));
        VIEW_MODES_NAMES.put(ViewMode.VM_CALENDAR_UPDATE, localizeService.getLocalizedResource("vm.calendarUpdate"));
        VIEW_MODES_NAMES.put(ViewMode.VM_CALENDAR_CREATE, localizeService.getLocalizedResource("vm.calendarCreate"));
    }

    public String getModeName(ViewMode mode) {
        return VIEW_MODES_NAMES.get(mode);
    }

    public ViewMode getModeValue(String value) {
        for (Map.Entry<ViewMode, String> entry: VIEW_MODES_NAMES.entrySet()) {
            if (entry.getValue().equals(value)) return entry.getKey();
        }
        throw new RuntimeException("Unknown View Mode");
    }

    public void setAllModes(ObservableList<ViewMode> list) {
        if (list.size() > 0) list.clear();
        list.addAll(VIEW_MODES_NAMES.keySet().stream().sorted().toList());
    }

    public ViewMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(ViewMode currentMode) {
        this.currentMode = currentMode;
    }
}
