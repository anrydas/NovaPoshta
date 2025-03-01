package das.tools.np.gui.controllers.windows;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.gui.CargoNumbersTableViewProducerService;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.gui.controllers.search.SearchControlsProduceService;
import das.tools.np.services.CommonService;
import das.tools.np.services.impl.LocalizeResourcesService;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.controlsfx.control.action.Action;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
public class WindowControlsProducer {
    private final CargoNumbersTableViewProducerService tableViewProducerService;
    private final SearchControlsProduceService controlsProduceService;
    private final FxWeaver fxWeaver;
    private final CommonService commonService;
    private final GlyphFont glyphFont;
    private final LocalizeResourcesService localizeService;

    public WindowControlsProducer(CargoNumbersTableViewProducerService tableViewProducerService, SearchControlsProduceService controlsProduceService, FxWeaver fxWeaver, CommonService commonService, GlyphFont glyphFont, LocalizeResourcesService localizeService) {
        this.tableViewProducerService = tableViewProducerService;
        this.controlsProduceService = controlsProduceService;
        this.fxWeaver = fxWeaver;
        this.commonService = commonService;
        this.glyphFont = glyphFont;
        this.localizeService = localizeService;
    }

    public WindowControlsHolder getNewControls() {
        WindowControlsHolder holder = getControls();
        MainController controller = fxWeaver.loadController(MainController.class);
        Stage stage = new Stage();
        stage.getIcons().add(controller.getWindowIcon());
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        holder.setStage(stage);
        AnchorPane root = holder.getRoot();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.getStylesheets().add(commonService.loadResource("/css/number-list-controls.css"));
        return holder;
    }

    private WindowControlsHolder getControls() {
        Label lbTitle = new Label();
        lbTitle.getStyleClass().add("caption-label");

        TableView<CargoNumber> dataTable = tableViewProducerService.getNewTable();
        dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        CheckBox chbSendAll = controlsProduceService.getSendAllItemsCheckbox();

        HBox hbTitle = new HBox(15, lbTitle, controlsProduceService.createPluginsMenuButton(dataTable, chbSendAll), chbSendAll);
        hbTitle.setAlignment(Pos.CENTER);
        AnchorPane.setLeftAnchor(hbTitle, 0.0);
        AnchorPane.setTopAnchor(hbTitle, 0.0);
        AnchorPane.setRightAnchor(hbTitle, 0.0);

        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(hbTitle, dataTable);
        AnchorPane.setLeftAnchor(dataTable, 0.0);
        AnchorPane.setTopAnchor(dataTable, 30.0);
        AnchorPane.setRightAnchor(dataTable, 0.0);
        AnchorPane.setBottomAnchor(dataTable, 0.0);

        return WindowControlsHolder.builder()
                .root(root)
                .titleLabel(lbTitle)
                .dataTable(dataTable)
                .build();
    }

    public List<Action> getActions(Consumer<ActionEvent> removeConsumer, Consumer<ActionEvent> clearConsumer) {
        List<Action> actions = new ArrayList<>(2);
        actions.add(new WindowAction(localizeService.getLocalizedResource("lw.menu.ctx.remove"),
                glyphFont.create(FontAwesome.Glyph.CROP),
                removeConsumer, null));
        actions.add(new WindowAction(localizeService.getLocalizedResource("lw.menu.ctx.clear"),
                glyphFont.create(FontAwesome.Glyph.CLOSE),
                clearConsumer, null));
        return actions;
    }
}
