package das.tools.np.entity.search;

import das.tools.np.gui.controllers.search.options.SearchCondition;

public interface ValueCondition {
    boolean isSelected();
    void setSelected(boolean selected);

    Object getValue();

    void setValue(Object value);

    void setCondition(SearchCondition condition);

    SearchCondition getCondition();
}
