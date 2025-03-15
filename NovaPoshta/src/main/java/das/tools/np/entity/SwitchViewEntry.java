package das.tools.np.entity;

import das.tools.np.gui.enums.SwitchControlType;
import javafx.scene.control.Control;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SwitchViewEntry {
    private SwitchControlType type;
    private Control control;
}
