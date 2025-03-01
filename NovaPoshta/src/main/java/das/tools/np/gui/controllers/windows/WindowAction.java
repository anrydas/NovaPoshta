package das.tools.np.gui.controllers.windows;

import das.tools.np.gui.menu.ActionService;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyCodeCombination;

import java.util.function.Consumer;

public class WindowAction extends ActionService.AppAction {
    public WindowAction(String text, Node image, Consumer<ActionEvent> eventHandler, KeyCodeCombination key) {
        super(text, image, eventHandler, key);
    }
}
