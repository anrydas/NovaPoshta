package das.tools.np.entity.search.values;

import das.tools.np.entity.search.ValueCondition;
import das.tools.np.gui.controllers.search.options.SearchCondition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class DateValueBox extends AbstractValueBox {
    private final DatePicker dpValue;

    public DateValueBox(String text, boolean restoreParams, ValueCondition value) {
        super(text, value.isSelected(), restoreParams, value, NO_CONTAINS_SEARCH_CONDITIONS);
        dpValue = new DatePicker();
        dpValue.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? DATE_FORMATTER.format(date) : "";
            }
            @Override
            public LocalDate fromString(String s) {
                return (s != null && !s.isEmpty()) ? LocalDate.parse(s, DATE_FORMATTER) : null;
            }
        });
        dpValue.valueProperty().addListener((o, v, n) -> value.setValue(n));
        if (restoreParams) {
            super.getConditionComboBox().setValue(value.getCondition());
            dpValue.setValue((LocalDate) value.getValue());
        } else {
            super.getConditionComboBox().setValue(SearchCondition.EQUAL);
            dpValue.setValue(LocalDate.now());
        }
    }

    public Node getControlsBox() {
        HBox box = new HBox(5, super.getControlsBox(), dpValue);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}
