package das.tools.np.entity.search.values;

import das.tools.np.entity.search.ValueCondition;
import das.tools.np.gui.controllers.search.options.SearchCondition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class AbstractValueBox {
    public static final SearchCondition[] SIMPLE_SEARCH_CONDITIONS = {SearchCondition.EQUAL, SearchCondition.NOT_EQUAL};
    public static final SearchCondition[] STRINGS_SEARCH_CONDITIONS = {SearchCondition.EQUAL, SearchCondition.NOT_EQUAL, SearchCondition.CONTAINS};
    public static final SearchCondition[] ALL_SEARCH_CONDITIONS = {SearchCondition.EQUAL, SearchCondition.NOT_EQUAL, SearchCondition.GREATER, SearchCondition.LOWER, SearchCondition.CONTAINS};
    public static final SearchCondition[] NO_CONTAINS_SEARCH_CONDITIONS = {SearchCondition.EQUAL, SearchCondition.NOT_EQUAL, SearchCondition.GREATER, SearchCondition.LOWER};
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final CheckBox checkBox;
    private final ComboBox<SearchCondition> conditionComboBox;
    public AbstractValueBox(String text, boolean selected, boolean restoreParams, ValueCondition value,
                     SearchCondition... conditions) {
        checkBox = getCheckBox(text, e -> value.setSelected(((CheckBox) e.getSource()).isSelected()), selected, restoreParams);
        checkBox.setTooltip(new Tooltip(text));
        conditionComboBox = getConditionCombo(conditions);
        conditionComboBox.getSelectionModel().selectedItemProperty().addListener((o, v, n) -> value.setCondition(n));
    }

    private CheckBox getCheckBox(String text, EventHandler<ActionEvent> handler, boolean selected, boolean restoreParams) {
        CheckBox chB = new CheckBox(text);
        chB.setOnAction(handler);
        if (restoreParams) {
            chB.setSelected(selected);
        }
        return chB;
    }

    private ComboBox<SearchCondition> getConditionCombo(SearchCondition... conditions) {
        ComboBox<SearchCondition> cb = new ComboBox<>();
        cb.getItems().addAll(conditions.length > 0 ? conditions : SearchCondition.values());
        cb.setStyle("-fx-font-size: 12; -fx-font-weight: bold");
        cb.setConverter(new StringConverter<>() {
            @Override
            public String toString(SearchCondition searchCondition) {
                return SearchCondition.stringValue(searchCondition);
            }
            @Override
            public SearchCondition fromString(String s) {
                return SearchCondition.fromString(s);
            }
        });
        return cb;
    }

    public Node getControlsBox() {
        HBox box = new HBox(5, checkBox, conditionComboBox);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}
