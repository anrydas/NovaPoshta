package das.tools.np.entity.search.values;


import das.tools.np.entity.db.CargoStatus;
import das.tools.np.entity.search.ValueCondition;
import das.tools.np.gui.controllers.search.options.SearchCondition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.util.List;

public class CargoStatusValueBox extends AbstractValueBox {
    private final ComboBox<CargoStatus> cbValue;

    public CargoStatusValueBox(String text, List<CargoStatus> items, ValueCondition value, boolean isRestore,
                               CargoStatus initialValue, StringConverter<CargoStatus> stringConverter) {
        super(text, value.isSelected(), isRestore, value, SIMPLE_SEARCH_CONDITIONS);;
        this.cbValue = new ComboBox<>();
        this.cbValue.setPrefWidth(150);
        this.cbValue.getItems().addAll(items);
        this.cbValue.valueProperty().addListener((o, v, n) -> value.setValue(n));
        this.cbValue.setConverter(stringConverter);
        if (isRestore) {
            super.getConditionComboBox().setValue(value.getCondition());
            cbValue.valueProperty().setValue(value.getValue() != null ? CargoStatus.valueOf(value.getValue().toString()) : CargoStatus.NEW);
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
