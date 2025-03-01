package das.tools.np.entity.search;

import das.tools.np.gui.controllers.search.options.SearchCondition;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter @ToString @EqualsAndHashCode
public class SearchDateValue implements SqlCondition, ValueCondition {
    @Builder.Default
    private boolean isSelected = false;
    @Builder.Default
    private LocalDate value = LocalDate.now();
    @Builder.Default
    private SearchCondition condition = SearchCondition.EQUAL;

    @Override
    public String getSqlCondition(String fieldName) {
        if (!isSelected) return DEFAULT_SQL_CONDITION;
        long epoch = value.toEpochSecond(LocalTime.now(), ZoneOffset.ofTotalSeconds(LocalDate.now().atStartOfDay().getSecond()));
        return String.format("%s %s %d", fieldName, SearchCondition.stringValue(condition), epoch);
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
        if (value instanceof LocalDate) {
            this.value = (LocalDate) value;
        } else {
            List<?> list = new ArrayList<>();
            if (value.getClass().isArray()) {
                list = Arrays.asList((Object[]) value);
            } else if (value instanceof Collection) {
                list = new ArrayList<>((Collection<?>) value);
            }
            int year = Integer.parseInt(list.get(0).toString());
            int month = Integer.parseInt(list.get(1).toString());
            int day = Integer.parseInt(list.get(2).toString());
            this.value = LocalDate.of(year, month, day);
        }
    }

    @Override
    public SearchCondition getCondition() {
        return condition;
    }
}
