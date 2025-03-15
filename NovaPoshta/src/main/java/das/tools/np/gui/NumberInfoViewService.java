package das.tools.np.gui;

import das.tools.np.entity.NameValue;
import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.SimpleNumber;
import das.tools.np.gui.dialog.ToastComponent;
import das.tools.np.gui.enums.AddsBoxTypes;
import das.tools.np.services.CargoStatusService;
import das.tools.np.services.CommonService;
import das.tools.np.services.LocalizeResourcesService;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Service
@Slf4j
public class NumberInfoViewService {
    private static final int MAX_LABEL_WIDTH = 550;
    private static final int MAX_TOOLTIP_TEXT_LENGTH = 80;
    private final InfoDataHolder defaultInfoData;
    private final ToastComponent toast;
    private final CommonService commonService;
    private final CargoStatusService numberService;
    private final LocalizeResourcesService localizeService;
    private final ConversionService conversionService;
    private final NumberListService listViewService;
    private final NumberInfoTableProduceService tableProduceService;
    private final GlyphFont glyphFont;

    public NumberInfoViewService(ToastComponent toast, CommonService commonService, CargoStatusService numberService, LocalizeResourcesService localizeService, ConversionService conversionService, NumberListService listViewService, NumberInfoTableProduceService tableProduceService, GlyphFont glyphFont) {
        this.toast = toast;
        this.commonService = commonService;
        this.numberService = numberService;
        this.localizeService = localizeService;
        this.conversionService = conversionService;
        this.listViewService = listViewService;
        this.tableProduceService = tableProduceService;
        this.glyphFont = glyphFont;
        this.defaultInfoData = InfoDataHolder.builder().label(false).copyButton(false).space(6).build();
    }

    public void clearFields(AnchorPane root) {
        root.getChildren().clear();
    }

    public void showInfo(AnchorPane root, CargoNumber number, boolean full) {
        clearFields(root);
        VBox vBox = new VBox(10);
        AnchorPane.setLeftAnchor(vBox, 1.0);
        AnchorPane.setRightAnchor(vBox, 1.0);
        AnchorPane.setTopAnchor(vBox, 1.0);
        AnchorPane.setBottomAnchor(vBox, 1.0);
        root.getChildren().add(vBox);

        vBox.getChildren().add(getAddsInfoLine(number));

        if (commonService.isNotEmpty(number.getComment())) {
            vBox.getChildren().add(getCommentBox(number));
        }
        if (commonService.isNotEmpty(number.getDescription())) {
            vBox.getChildren().add(getDescriptionBox(number));
        }
        vBox.getChildren().addAll(
                getBox(defaultInfoData,
                        getGroupBox(number),
                        getStatusBox(number),
                        getTypeBox(number)
                ),
                getCostBox(number),
                getDatesBox(number),
                getRecipientBox(number),
                getRecipientAddressBox(number),
                getSenderBox(number),
                getSenderAddressBox(number),
                getBarcodeBox(number)
        );
        if (full) {
            /* ToDo: Remove it?
            Line line = new Line(10,10,300,10);
            line.getStrokeDashArray().addAll(25d, 10d);
            line.setStrokeWidth(3);
            line.setStroke(Color.DARKGRAY);
            vBox.getChildren().add(getBox(defaultInfoData, line));
            */
            HBox detailDox = getBox(InfoDataHolder.builder()
                    .label(false)
                    .text(localizeService.getLocalizedResource("info.label.detailed"))
                    .textWeight(FontWeight.BOLD)
                    .build());
            vBox.getChildren().add(detailDox);
            vBox.getChildren().add(getTable(number));
        }
    }

    private TreeTableView<NameValue> getTable(CargoNumber number) {
        return tableProduceService.getTable(number);
    }

    private HBox getAddsInfoLine(CargoNumber number) {
        SimpleNumber simpleNumber = conversionService.convert(number, SimpleNumber.class);
        return listViewService.getAddsLine(Objects.requireNonNull(simpleNumber), AddsBoxTypes.NUMBER_INFO);
    }

    private HBox getCommentBox(CargoNumber number) {
        return getBox(InfoDataHolder.builder()
                .label(false).copyButton(false)
                .text(number.getComment())
                .tooltip(localizeService.getLocalizedResource("info.tooltip.comment"))
                .build());
    }

    private HBox getGroupBox(CargoNumber number) {
        InfoDataHolder h1 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.group")).text(number.getGroup().getName())
                .tooltip(localizeService.getLocalizedResource("info.tooltip.group"))
                .build();
        return getBox(h1);
    }

    private HBox getDescriptionBox(CargoNumber number) {
        return getBox(InfoDataHolder.builder()
                .label(false).copyButton(true)
                .text(number.getDescription()).fontSize(12)
                .textWeight(FontWeight.NORMAL)
                .tooltip(localizeService.getLocalizedResource("info.tooltip.descr"))
                .build());
    }

    private HBox getStatusBox(CargoNumber number) {
        InfoDataHolder h1 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.status")).text(number.getFullData().getStatus())
                .textColor(numberService.getStatusTextColor(number.getStatusCode()))
                .tooltip(localizeService.getLocalizedResource("info.tooltip.status") + ": " + number.getFullData().getStatus())
                .build();
        return getBox(defaultInfoData, getBox(h1));
    }

    private HBox getTypeBox(CargoNumber number) {
        InfoDataHolder h1 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.type")).text(number.getCargoType())
                .tooltip(localizeService.getLocalizedResource("info.tooltip.type"))
                .build();
        return getBox(defaultInfoData, getBox(h1));
    }

    private HBox getCostBox(CargoNumber number) {
        InfoDataHolder h1 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.cost")).text(String.valueOf(number.getCost()))
                .tooltip(localizeService.getLocalizedResource("info.tooltip.cost"))
                .build();
        InfoDataHolder h2 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.seats")).text(String.format("%.0f", number.getSeatsAmount()))
                .tooltip(localizeService.getLocalizedResource("info.tooltip.seats"))
                .build();
        InfoDataHolder h3 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.price")).text(String.valueOf(number.getAnnouncedPrice()))
                .tooltip(localizeService.getLocalizedResource("info.tooltip.price"))
                .build();
        InfoDataHolder h4 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.weight")).text(String.format("%.2f", number.getWeight()))
                .tooltip(localizeService.getLocalizedResource("info.tooltip.weight"))
                .build();
        return getBox(defaultInfoData, getBox(h1), getBox(h2), getBox(h3), getBox(h4));
    }

    private HBox getDatesBox(CargoNumber number) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        InfoDataHolder h1 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.created")).text(number.getDateCreated()).fontSize(12)
                .tooltip(localizeService.getLocalizedResource("info.tooltip.created"))
                .build();
        InfoDataHolder h2 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.scheduled")).text(number.getScheduledDeliveryDate()).fontSize(12)
                .tooltip(localizeService.getLocalizedResource("info.tooltip.scheduled"))
                .build();
        InfoDataHolder h3 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.updated")).text(number.getUpdated() != null ? sdf.format(number.getUpdated()) : "").fontSize(12)
                .tooltip(localizeService.getLocalizedResource("info.tooltip.updated"))
                .build();
        return getBox(defaultInfoData, getBox(h1), getBox(h2), getBox(h3));
    }

    private HBox getRecipientBox(CargoNumber number) {
        InfoDataHolder h1 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.recipient.name")).text(number.getRecipientFullName())
                .tooltip(localizeService.getLocalizedResource("info.tooltip.recipient.name"))
                .build();
        InfoDataHolder h2 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.recipient.city")).text(number.getCityRecipient())
                .tooltip(localizeService.getLocalizedResource("info.tooltip.recipient.city"))
                .build();
        return getBox(defaultInfoData, getBox(h1), getBox(h2));
    }

    private HBox getRecipientAddressBox(CargoNumber number) {
        InfoDataHolder h1 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.recipient.warehouse")).text(number.getWarehouseRecipient()).fontSize(12)
                .tooltip(localizeService.getLocalizedResource("info.tooltip.recipient.warehouse"))
                .build();
        return getBox(defaultInfoData, getBox(h1));
    }

    private HBox getSenderBox(CargoNumber number) {
        InfoDataHolder h1 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.sender.name")).text(number.getFullData().getSenderFullNameEW())
                .tooltip(localizeService.getLocalizedResource("info.tooltip.sender.name"))
                .build();
        InfoDataHolder h2 = InfoDataHolder.builder()
                .label(false)
                .text(commonService.isNotEmpty(number.getPhoneSender()) ? String.format(localizeService.getLocalizedResource("info.label.sender.phone"), number.getPhoneSender()) : "")
                .copyButton(true)
                .tooltip(localizeService.getLocalizedResource("info.tooltip.sender.phone"))
                .build();
        InfoDataHolder h3 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.sender.city")).text(number.getCitySender())
                .tooltip(localizeService.getLocalizedResource("info.tooltip.sender.city"))
                .build();
        return getBox(defaultInfoData, getBox(h1), getBox(h2), getBox(h3));
    }

    private HBox getSenderAddressBox(CargoNumber number) {
        InfoDataHolder h1 = InfoDataHolder.builder()
                .labelText(localizeService.getLocalizedResource("info.label.sender.warehouse")).text(number.getWarehouseSender())
                .fontSize(12)
                .tooltip(localizeService.getLocalizedResource("info.tooltip.sender.warehouse"))
                .build();
        return getBox(defaultInfoData, getBox(h1));
    }

    private HBox getBarcodeBox(CargoNumber number) {
        try {
            Barcode barcode = BarcodeFactory.createCode128(number.getNumber());
            barcode.setDrawingQuietSection(false);
            barcode.setDrawingText(false);
            ImageView iv = new ImageView(SwingFXUtils.toFXImage(BarcodeImageHandler.getImage(barcode), null));
            return getBox(InfoDataHolder.builder()
                    .tooltip(localizeService.getLocalizedResource("info.tooltip.barcode"))
                    .build(), iv);
        } catch (BarcodeException | OutputException e) {
            log.error(String.format("Error creating barcode for '%s': ", number.getNumber()), e);
        }
        return getBox(defaultInfoData);
    }

    private HBox getBox(InfoDataHolder h, Node... nodes) {
        HBox hb = new HBox(h.getSpace());
        AnchorPane.setLeftAnchor(hb, 1.0);
        AnchorPane.setRightAnchor(hb, 1.0);
        hb.setAlignment(h.getAlign());
        if (h.isLabel() && commonService.isNotEmpty(h.getText())) {
            hb.getChildren().add(new Label(h.getLabelText()));
        }
        hb.getChildren().addAll(nodes);
        if (commonService.isNotEmpty(h.getText())) {
            Label label = new Label(h.getText());
            label.setMaxWidth(MAX_LABEL_WIDTH);
            label.setFont(new Font(h.fontSize));
            label.setTextOverrun(OverrunStyle.ELLIPSIS);
            label.setStyle(String.format("-fx-font-weight: %s; -fx-text-fill: %s;", h.getTextWeight().toString(), h.getTextColor()));
            hb.getChildren().add(label);
            if (h.isCopyButton()) {
                hb.getChildren().add(getCopyButton(label));
            }
        }
        if (commonService.isNotEmpty(h.getTooltip())) {
            String text = commonService.splitStringAsPhonemes(h.getTooltip(), MAX_TOOLTIP_TEXT_LENGTH);
            Tooltip.install(hb, new Tooltip(text));
        }
        return hb;
    }

    public Labeled getCopyButton(Labeled control) {
        Label lb = getLabel(FontAwesome.Glyph.COPY);
        lb.setOnMouseClicked(e -> {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(control.getText()), null);
            toast.makeToast((Stage) control.getScene().getWindow(),
                    localizeService.getLocalizedResource("info.button.message.copy"));
        });
        installTooltip(lb, "info.button.tooltip.copy");
        return lb;
    }

    public Labeled getOpenButton() {
        Label lb = getLabel(FontAwesome.Glyph.EXTERNAL_LINK);
        installTooltip(lb, "info.button.tooltip.open");
        return lb;
    }

    private Label getLabel(FontAwesome.Glyph glyph) {
        Label lb = new Label("", glyphFont.create(glyph).color(Color.rgb(0xff, 0xdd, 0x00)).size(24));
        lb.setPrefWidth(30);
        lb.setPrefHeight(30);
        lb.setAlignment(Pos.TOP_CENTER);
        lb.setEffect(getShadow());
        CornerRadii corn = new CornerRadii(5);
        Background bg = new Background(new BackgroundFill(Color.rgb(0x00, 0x57, 0xb7), corn, Insets.EMPTY));
        lb.setBackground(bg);
        return lb;
    }

    private static DropShadow getShadow() {
        return new DropShadow(3, 3, 3, Color.DARKGREY);
    }

    public void installTooltip(Node node, String tooltipKey) {
        Tooltip.install(node, new Tooltip(localizeService.getLocalizedResource(tooltipKey)));
    }

    public void invalidateComboBox(ComboBox<?> cb) {
        int selectedIndex = cb.getSelectionModel().getSelectedIndex();
        cb.getSelectionModel().selectNext();
        cb.getSelectionModel().select(selectedIndex);
    }

    @Builder @Getter @Setter @AllArgsConstructor
    private static class InfoDataHolder {
        @Builder.Default
        private int space = 3;
        @Builder.Default
        private boolean label = true;
        @Builder.Default
        private boolean copyButton = false;
        private String labelText;
        @Builder.Default
        private int fontSize = 14;
        private String text;
        @Builder.Default
        private FontWeight textWeight = FontWeight.BOLD;
        @Builder.Default
        private String textColor = "black";
        private String tooltip;
        @Builder.Default
        private Pos align = Pos.CENTER;
    }
}
