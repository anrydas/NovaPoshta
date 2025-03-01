package das.tools.np.gui;

import das.tools.np.entity.NameValue;
import das.tools.np.entity.db.CargoNumber;
import das.tools.np.gui.menu.NumberInfoTableMenuService;
import das.tools.np.services.LocalizeResourcesService;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service @Scope("prototype")
@Slf4j
public class NumberInfoTableProduceService {
    public static final int NAME_COLUMN_MIN_WIDTH = 250;
    public static final int VALUE_COLUMN_MIN_WIDTH = 400;
    private final LocalizeResourcesService localizeService;
    private final FieldNameService fieldNameService;
    private final NumberInfoTableMenuService menuService;

    public NumberInfoTableProduceService(LocalizeResourcesService localizeService, FieldNameService fieldNameService, NumberInfoTableMenuService menuService) {
        this.localizeService = localizeService;
        this.fieldNameService = fieldNameService;
        this.menuService = menuService;
    }

    public TreeTableView<NameValue> getTable(CargoNumber number) {
        TreeTableView<NameValue> tv = new TreeTableView<>();
        TreeTableColumn<NameValue,String> propName = new TreeTableColumn<>(localizeService.getLocalizedResource("info.field.detailed.name"));
        TreeTableColumn<NameValue,String> propValue = new TreeTableColumn<>(localizeService.getLocalizedResource("info.field.detailed.value"));
        propName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        propValue.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        propName.setMinWidth(NAME_COLUMN_MIN_WIDTH);
        propValue.setMinWidth(VALUE_COLUMN_MIN_WIDTH);
        tv.getColumns().add(propName);
        tv.getColumns().add(propValue);
        TreeItem<NameValue> root = new TreeItem<>(new NameValue(number.getNumber(), ""));
        for (Field field: number.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(number);
                String fieldName = field.getName();
                if (fieldNameService.isGroupedField(fieldName)) {
                    TreeItem<NameValue> newGroup = getTreeItem(fieldNameService.getFieldFullName(fieldName), "");
                    for (Field f: field.getType().getDeclaredFields()) {
                        f.setAccessible(true);
                        Object fValue = f.get(value);
                        TreeItem<NameValue> subItem = getTreeItem(f.getName(), fValue);
                        newGroup.getChildren().add(subItem);
                    }
                    root.getChildren().add(newGroup);
                } else {
                    TreeItem<NameValue> item = getTreeItem(fieldNameService.getFieldFullName(fieldName), value);
                    root.getChildren().add(item);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        tv.setRoot(root);
        root.setExpanded(true);
        ContextMenu contextMenu = menuService.getContextMenu(tv);
        tv.setContextMenu(contextMenu);
        return tv;
    }

    private TreeItem<NameValue> getTreeItem(String name, Object value) {
        return new TreeItem<>(new NameValue(name, String.valueOf(value != null ? value : "")));
    }
}
