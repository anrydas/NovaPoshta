package das.tools.np.gui.controllers.windows;

import das.tools.np.entity.db.CargoNumber;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class WindowControlsHolder {
    private Stage stage;
    private AnchorPane root;
    private Label titleLabel;
    private TableView<CargoNumber> dataTable;
}
