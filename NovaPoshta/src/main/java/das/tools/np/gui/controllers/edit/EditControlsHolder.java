package das.tools.np.gui.controllers.edit;

import das.tools.np.entity.db.ExtraPhone;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.*;
import org.controlsfx.control.ListActionView;


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
