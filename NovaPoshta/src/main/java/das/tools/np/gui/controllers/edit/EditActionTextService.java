package das.tools.np.gui.controllers.edit;

import das.tools.np.services.LocalizeResourcesService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EditActionTextService {
    private final Map<EditAction,String> ACTION_TEXT = new HashMap<>(EditAction.values().length);
    private final LocalizeResourcesService localizeService;

    @PostConstruct
    public void initData() {
        ACTION_TEXT.put(EditAction.MOVE_UP, "edit.options.action.moveUp");
        ACTION_TEXT.put(EditAction.MOVE_DOWN, "edit.options.action.moveDn");
        ACTION_TEXT.put(EditAction.ADD, "edit.options.action.add");
        ACTION_TEXT.put(EditAction.EDIT, "edit.options.action.edit");
        ACTION_TEXT.put(EditAction.APPLY, "edit.options.action.apply");
        ACTION_TEXT.put(EditAction.REMOVE, "edit.options.action.remove");
    }

    public EditActionTextService(LocalizeResourcesService localizeService) {
        this.localizeService = localizeService;
    }

    public String getActionText(EditAction editAction) {
        return localizeService.getLocalizedResource(ACTION_TEXT.get(editAction));
    }
}
