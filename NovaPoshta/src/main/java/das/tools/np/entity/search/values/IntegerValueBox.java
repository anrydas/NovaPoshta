package das.tools.np.entity.search.values;

import das.tools.np.entity.search.ValueCondition;
import das.tools.np.gui.controllers.search.options.SearchCondition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

public class IntegerValueBox extends AbstractValueBox {
    private final Spinner<Integer> spValue;
    public IntegerValueBox(String text,int max, int initialValue, int step, boolean restoreParams, ValueCondition value) {
        super(text, value.isSelected(), restoreParams, value, NO_CONTAINS_SEARCH_CONDITIONS);
        this.spValue = new Spinner<>(0, max, 0, step);
        this.spValue.valueProperty().addListener((o, v, n) -> value.setValue(n));
        this.spValue.setEditable(true);
        if (restoreParams) {
            super.getConditionComboBox().setValue(value.getCondition());
            spValue.getValueFactory().valueProperty().setValue((Integer) value.getValue());
        } else {
            super.getConditionComboBox().setValue(SearchCondition.EQUAL);
            spValue.getValueFactory().valueProperty().setValue(initialValue);
        }
    }

    public Node getControlsBox() {
        HBox box = new HBox(5, super.getControlsBox(), spValue);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}
