package das.tools.np.gui;

import das.tools.np.entity.db.SimpleNumber;
import das.tools.np.gui.enums.AddsBoxTypes;
import das.tools.np.services.CargoStatusService;
import das.tools.np.services.CargoTypeService;
import das.tools.np.services.CommonService;
import das.tools.np.services.LocalizeResourcesService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
@Slf4j
public class NumberListService {
    protected static final SimpleDateFormat ITEM_DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
    protected static final int LABEL_FONT_SIZE = 10;
    protected static final int INNER_BOX_SPACING = 2;
    protected static final int BOX_SPACING = 7;
    protected static final int IMAGE_SIZE = 16;
    protected static final int BIG_IMAGE_SIZE = 24;
    private final CommonService commonService;
    private final CargoStatusService statusService;
    private final CargoTypeService typeService;
    private final LocalizeResourcesService localizeService;
    private final GlyphFont glyphFont;

    public NumberListService(CommonService commonService, CargoStatusService statusService, CargoTypeService typeService, LocalizeResourcesService localizeService, GlyphFont glyphFont) {
        this.commonService = commonService;
        this.statusService = statusService;
        this.typeService = typeService;
        this.localizeService = localizeService;
        this.glyphFont = glyphFont;
    }

    public ListCell<SimpleNumber> getNumberListView() {
        return new ListCell<SimpleNumber>(){
            @Override
            protected void updateItem(SimpleNumber number, boolean empty) {
                super.updateItem(number, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    setGraphic(makeListElement(number));
                }
            }
        };
    }

    public Node makeListElement(SimpleNumber number) {
        Label lNum = getStyledLabel(number.getNumber(), 18);
        //ImageView imgStatus = commonService.getImageView(statusService.getBigStatusImage(number.getAppStatus()), BIG_IMAGE_SIZE);
        Label imgStatus = new Label("", glyphFont.create(statusService.getStatusGlyph(number.getAppStatus()))
                .sizeFactor(2).color(statusService.getStatusTextColor(number.getAppStatus())));
        HBox hNumber = new HBox(lNum, imgStatus);
        hNumber.setSpacing(10);
        hNumber.setPrefHeight(32);

        String txt = commonService.isNotEmpty(number.getComment()) ? number.getComment() : number.getDescription();
        Label comment = getStyledLabel(txt, LABEL_FONT_SIZE);
        HBox hComment = new HBox(comment);

        return new VBox(hNumber, hComment, getAddsLine(number, AddsBoxTypes.NUMBERS_LIST));
    }

    public HBox getAddsLine(SimpleNumber number, AddsBoxTypes boxType) {
        HBox hT = getBoxControl(typeService.getTypeGlyph(number.getNumberType()), "",
                String.format(localizeService.getLocalizedResource("item.tooltip.type"), typeService.getTypeName(number.getNumberType())),
                typeService.getTypeColor(number.getNumberType()));
        HBox hS = getBoxControl(statusService.getStatusGlyph(number.getAppStatus()), "",
                String.format(localizeService.getLocalizedResource("item.tooltip.status"), statusService.getStatusName(number.getAppStatus())),
                statusService.getStatusTextColor(number.getAppStatus()));
        HBox hC = getBoxControl(FontAwesome.Glyph.CIRCLE_ALT, ITEM_DATE_FORMAT.format(number.getCreateDate()),
                localizeService.getLocalizedResource("item.tooltip.created.date"), Color.MEDIUMBLUE);
        HBox hA = getBoxControl(FontAwesome.Glyph.DOT_CIRCLE_ALT, ITEM_DATE_FORMAT.format(number.getCreated()),
                localizeService.getLocalizedResource("item.tooltip.added.date"), Color.GREEN);
        HBox hU = getBoxControl(FontAwesome.Glyph.CIRCLE, ITEM_DATE_FORMAT.format(number.getUpdated()),
                localizeService.getLocalizedResource("item.tooltip.updated.date"), Color.ORANGERED);

        Pane space = new Pane();
        space.setPrefHeight(IMAGE_SIZE);
        space.setPrefWidth((hT.getBoundsInParent().getWidth() +
                hA.getBoundsInParent().getWidth() +
                hC.getBoundsInParent().getWidth() +
                hU.getBoundsInParent().getWidth() +
                hS.getBoundsInParent().getWidth()) / 5);
        HBox hBox;
        if (AddsBoxTypes.NUMBER_INFO.equals(boxType)) {
            hBox = new HBox(BOX_SPACING, hT, hS, hC, hA, hU);
            hBox.setAlignment(Pos.CENTER);
        } else if (AddsBoxTypes.NUMBERS_LIST.equals(boxType)) {
            hBox = new HBox(BOX_SPACING, space, hT, hS, hC, hA, hU);
            hBox.setAlignment(Pos.CENTER_LEFT);
        } else {
            throw new RuntimeException("Unknown Adds Box type: " + boxType);
        }
        return hBox;
    }

    private HBox getBoxControl(String img, String label, String tooltip) {
        HBox hb = new HBox(INNER_BOX_SPACING);
        if (commonService.isNotEmpty(img)) {
            ImageView iv = commonService.getImageView(img, IMAGE_SIZE);
            hb.getChildren().add(iv);
        }
        if (commonService.isNotEmpty(label)) {
            Label lb = getStyledLabel(label, LABEL_FONT_SIZE);
            hb.getChildren().add(lb);
        }
        Tooltip.install(hb, new Tooltip(tooltip));
        return hb;
    }

    private HBox getBoxControl(FontAwesome.Glyph img, String label, String tooltip, Color imageColor) {
        HBox hb = new HBox(INNER_BOX_SPACING);
        hb.setAlignment(Pos.BOTTOM_LEFT);
        Label lbImage = new Label("", glyphFont.create(img).color(imageColor));
        hb.getChildren().add(lbImage);
        if (commonService.isNotEmpty(label)) {
            Label lb = getStyledLabel(label, LABEL_FONT_SIZE);
            hb.getChildren().add(lb);
        }
        Tooltip.install(hb, new Tooltip(tooltip));
        return hb;
    }

    private Label getStyledLabel(String text, int fontSize) {
        Label lb = new Label(text);
        lb.setStyle(String.format("-fx-font-size: %d", fontSize));
        return lb;
    }
}
