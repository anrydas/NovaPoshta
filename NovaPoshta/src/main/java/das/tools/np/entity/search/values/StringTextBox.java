package das.tools.np.entity.search.values;

import das.tools.np.entity.search.ValueCondition;
import das.tools.np.gui.controllers.search.options.SearchCondition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class StringTextBox extends AbstractValueBox {
    private final TextField edValue;
    public StringTextBox(String text, boolean restoreParams,
                         ValueCondition value, String initialValue) {
        super(text, value.isSelected(), restoreParams, value, STRINGS_SEARCH_CONDITIONS);
        this.edValue = new TextField();
        this.edValue.setPrefWidth(150);
        this.edValue.textProperty().addListener((o, v, n) -> value.setValue(n));
        if (restoreParams) {
            super.getConditionComboBox().setValue(value.getCondition());
            edValue.textProperty().setValue((String) value.getValue());
        } else {
            super.getConditionComboBox().setValue(SearchCondition.EQUAL);
            edValue.textProperty().setValue(initialValue);
        }
    }

    public Node getControlsBox() {
        HBox box = new HBox(5, super.getControlsBox(), edValue);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}
