package das.tools.np.entity.search.values;

import das.tools.np.entity.search.ValueCondition;
import das.tools.np.gui.controllers.search.options.SearchCondition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

import java.util.List;

public class StringValueBox extends AbstractValueBox {
    private final ComboBox<String> cbValue;
    public StringValueBox(String text, List<String> items, boolean restoreParams,
                          ValueCondition value, String initialValue) {
        super(text, value.isSelected(), restoreParams, value, SIMPLE_SEARCH_CONDITIONS);
        this.cbValue = new ComboBox<>();
        this.cbValue.setPrefWidth(150);
        this.cbValue.getItems().addAll(items);
        this.cbValue.valueProperty().addListener((o, v, n) -> value.setValue(n));
        if (restoreParams) {
            super.getConditionComboBox().setValue(value.getCondition());
            cbValue.valueProperty().setValue((String) value.getValue());
        } else {
            super.getConditionComboBox().setValue(SearchCondition.EQUAL);
            cbValue.valueProperty().setValue(initialValue);
        }
    }

    public Node getControlsBox() {
        HBox box = new HBox(5, super.getControlsBox(), cbValue);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}
