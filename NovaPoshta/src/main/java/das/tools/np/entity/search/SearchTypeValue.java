package das.tools.np.entity.search;

import das.tools.np.entity.db.CargoStatus;
import das.tools.np.entity.db.NumberType;
import das.tools.np.entity.db.Status;
import das.tools.np.gui.controllers.search.options.SearchCondition;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString @EqualsAndHashCode
@Slf4j
public class SearchTypeValue<T> implements SqlCondition, ValueCondition {
    private boolean isSelected = false;
    private T value;
    private SearchCondition condition = SearchCondition.EQUAL;

    @Override
    public String getSqlCondition(String fieldName) {
        if (!isSelected) return DEFAULT_SQL_CONDITION;
        if (value instanceof CargoStatus) {
            return String.format("%s %s %d", fieldName, SearchCondition.stringValue(condition), ((CargoStatus) value).ordinal());
        } else if (value instanceof NumberType) {
            return String.format("%s %s %d", fieldName, SearchCondition.stringValue(condition), ((NumberType) value).ordinal());
        } else {
            return String.format("%s %s %d", fieldName, SearchCondition.stringValue(condition), Status.valueOf((String) value).ordinal());
        }
    }

    @Override
    public void setCondition(SearchCondition condition) {
        this.condition = condition;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (T) value;
    }

    @Override
    public SearchCondition getCondition() {
        return condition;
    }
}
