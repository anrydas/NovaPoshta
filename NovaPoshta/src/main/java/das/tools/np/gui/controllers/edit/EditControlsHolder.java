package das.tools.np.gui.controllers.edit;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import lombok.*;


@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class EditControlsHolder<T> {
    private AnchorPane root;
    private Button btOk;
    private Button btCancel;
    private ListView<T> listView;
}
