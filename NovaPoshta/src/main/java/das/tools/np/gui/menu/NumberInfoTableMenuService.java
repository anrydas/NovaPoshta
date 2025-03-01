package das.tools.np.gui.menu;

import das.tools.np.entity.NameValue;
import das.tools.np.gui.dialog.ToastComponent;
import das.tools.np.services.LocalizeResourcesService;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

@Service
@Slf4j
public class NumberInfoTableMenuService {
    public static final String KV_STRING_FORMAT = "%s=%s";
    public static final String KV1_STRING_FORMAT = "%s\t%s";
    private final CommonMenuService commonMenu;
    private final ToastComponent toast;
    private final LocalizeResourcesService localizeService;

    public NumberInfoTableMenuService(CommonMenuService commonMenu, ToastComponent toast, LocalizeResourcesService localizeService) {
        this.commonMenu = commonMenu;
        this.toast = toast;
        this.localizeService = localizeService;
    }

    public ContextMenu getContextMenu(TreeTableView<NameValue> treeTableView) {
        return new ContextMenu(
                getCopyKeyItem(treeTableView),
                getCopyValueItem(treeTableView),
                getCopyKeyValueItem(treeTableView),
                getCopyKeyValueItem1(treeTableView),
                getCopyAll(treeTableView));
    }

    private MenuItem getCopyKeyItem(TreeTableView<NameValue> treeTableView) {
        return commonMenu.getMenuItem(localizeService.getLocalizedResource("info.field.menu.copy.name"),
                e -> copyRowKey(treeTableView),
                new KeyCodeCombination(KeyCode.K, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
    }

    private MenuItem getCopyValueItem(TreeTableView<NameValue> treeTableView) {
        return commonMenu.getMenuItem(localizeService.getLocalizedResource("info.field.menu.copy.value"),
                e -> copyRowValue(treeTableView),
                new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
    }

    private MenuItem getCopyKeyValueItem(TreeTableView<NameValue> treeTableView) {
        return commonMenu.getMenuItem(localizeService.getLocalizedResource("info.field.menu.copy.kv"),
                e -> copyRowKeyValue(treeTableView),
                new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
    }

    private MenuItem getCopyKeyValueItem1(TreeTableView<NameValue> treeTableView) {
        return commonMenu.getMenuItem(localizeService.getLocalizedResource("info.field.menu.copy.kv1"),
                e -> copyRowKeyValue1(treeTableView),
                new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
    }

    private MenuItem getCopyAll(TreeTableView<NameValue> treeTableView) {
        return commonMenu.getMenuItem(localizeService.getLocalizedResource("info.field.menu.copy.all"),
                e -> copyAll(treeTableView),
                new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
    }

    private TreeItem<NameValue> getSelectedItem(TreeTableView<NameValue> treeView) {
        return treeView.getSelectionModel().getSelectedItem();
    }

    private void copyRowKey(TreeTableView<NameValue> treeTableView) {
        copyToClip(treeTableView, getSelectedItem(treeTableView).getValue().getName());
    }

    private void copyRowValue(TreeTableView<NameValue> treeTableView) {
        copyToClip(treeTableView, getSelectedItem(treeTableView).getValue().getValue());
    }

    private void copyRowKeyValue(TreeTableView<NameValue> treeTableView) {
        copyToClip(treeTableView, String.format(KV_STRING_FORMAT, getSelectedItem(treeTableView).getValue().getName(),
                getSelectedItem(treeTableView).getValue().getValue()));
    }

    private void copyRowKeyValue1(TreeTableView<NameValue> treeTableView) {
        copyToClip(treeTableView, String.format(KV1_STRING_FORMAT, getSelectedItem(treeTableView).getValue().getName(),
                getSelectedItem(treeTableView).getValue().getValue()));
    }

    private void copyAll(TreeTableView<NameValue> treeTableView) {
        StringBuilder sb = new StringBuilder();
        populateStrings(treeTableView.getRoot(), sb);
        copyToClip(treeTableView, sb.toString());
    }

    private void populateStrings(TreeItem<NameValue> item, StringBuilder sb) {
        if(item.getChildren().size() > 0) {
            for(TreeItem<NameValue> subItem : item.getChildren()) {
                populateStrings(subItem, sb);
            }
        } else {
            sb.append(String.format(KV_STRING_FORMAT, item.getValue().getName(), item.getValue().getValue())).append("\n");
        }
    }

    private void copyToClip(TreeTableView<NameValue> treeTableView, String value) {
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(value), null);
        toast.makeToast((Stage) treeTableView.getScene().getWindow(),
                localizeService.getLocalizedResource("info.button.message.copy"));
    }
}
