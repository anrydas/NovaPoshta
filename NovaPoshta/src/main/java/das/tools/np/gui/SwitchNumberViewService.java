package das.tools.np.gui;

import das.tools.np.entity.SwitchViewEntry;
import das.tools.np.gui.enums.SwitchControlType;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SwitchNumberViewService {
    private final List<SwitchViewEntry> controls = new ArrayList<>();

    public void addControl(SwitchControlType type, Control control) {
        controls.add(new SwitchViewEntry(type, control));
    }

    public void addControls(Control... controls) {
        SwitchControlType type = SwitchControlType.SC_NONE;
        for (Control control: controls) {
            if (control instanceof TreeView<?>) {
                type = SwitchControlType.SC_TREE;
            } else if (control instanceof ListView<?>) {
                type = SwitchControlType.SC_LIST;
            }
            this.controls.add(new SwitchViewEntry(type, control));
        }
    }

    public SwitchControlType getControlType(Control control) {
        SwitchViewEntry entry = findEntry(control);
        return entry != null ? entry.getType() : SwitchControlType.SC_NONE;

    }

    public Control getVisibleControl() {
        for (SwitchViewEntry entry: controls) {
            if (entry.getControl().isVisible()) {
                return entry.getControl();
            }
        }
        return null;
    }

    public void makeControlVisible(Control control) {
        for (SwitchViewEntry entry: controls) {
            entry.getControl().setVisible(entry.getControl().equals(control));
        }
    }

    private SwitchViewEntry findEntry(Control control) {
        for (SwitchViewEntry entry: controls) {
            if (entry.getControl().equals(control)) return entry;
        }
        return null;
    }
}
