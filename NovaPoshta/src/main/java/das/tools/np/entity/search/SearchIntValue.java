package das.tools.np.entity.search;

import das.tools.np.gui.controllers.search.options.SearchCondition;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter @ToString @EqualsAndHashCode
public class SearchIntValue implements SqlCondition, ValueCondition {
    @Builder.Default
    private boolean isSelected = false;
    @Builder.Default
    private int value = 0;
    @Builder.Default
    private SearchCondition condition = SearchCondition.EQUAL;

    @Override
    public String getSqlCondition(String fieldName) {
        if (!isSelected) return DEFAULT_SQL_CONDITION;
        return String.format("%s %s %d", fieldName, SearchCondition.stringValue(condition), value);
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
        this.value = (Integer) value;
    }

    @Override
    public SearchCondition getCondition() {
        return condition;
    }
}
