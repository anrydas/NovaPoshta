package das.tools.np.entity.search;

import das.tools.np.gui.controllers.search.options.SearchCondition;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter @ToString @EqualsAndHashCode
public class SearchStringValue implements SqlCondition, ValueCondition {
    @Builder.Default
    private boolean isSelected = false;
    @Builder.Default
    private String value = "";
    @Builder.Default
    private SearchCondition condition = SearchCondition.EQUAL;

    @Override
    public String getSqlCondition(String fieldName) {
        if (!isSelected) return DEFAULT_SQL_CONDITION;
        String result = "";
        if (condition.equals(SearchCondition.CONTAINS)) {
            result = String.format("lower(%s) like '%%%s%%'", fieldName, value);
        } else {
            result = String.format("lower(%s) %s '%%%s%%'", fieldName, SearchCondition.stringValue(condition), value);
        }
        return result;
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
        this.value = (String) value;
    }

    @Override
    public SearchCondition getCondition() {
        return condition;
    }
}
