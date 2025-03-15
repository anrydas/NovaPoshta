package das.tools.np.gui;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.SimpleNumber;
import das.tools.np.gui.controllers.AddNumberController;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.services.CommonService;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NumberTreeViewService {

    public static final String ROOT_ITEM_NAME = "/";
    public static final String GROUP_IMAGE = "/images/number/group_24.png";
    private static final int GROUP_LABEL_FONT_SIZE = 12;

    private final NumberListService listViewService;
    private final CargoNumberRepository numberRepository;
    private final ConversionService conversionService;
    private final CommonService commonService;
    private final GlyphFont glyphFont;

    public NumberTreeViewService(NumberListService listViewService, CargoNumberRepository numberRepository, ConversionService conversionService, CommonService commonService, GlyphFont glyphFont) {
        this.listViewService = listViewService;
        this.numberRepository = numberRepository;
        this.conversionService = conversionService;
        this.commonService = commonService;
        this.glyphFont = glyphFont;
    }

    public TreeCell<String> getTreeView() {
        return new TreeCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (ROOT_ITEM_NAME.equals(getItem())) {
                        setText(ROOT_ITEM_NAME);
                    } else if (!AddNumberController.NUMBER_PATTERN.matcher(item).find()) {
                        // Group
                        //setText(item);
                        setGraphic(getGroupItem(item));
                    } else {
                        // Number
                        setText(null);
                        setGraphic(makeNumberTreeElement(item));
                    }
                }
            }
        };
    }

    private Node makeNumberTreeElement(String number) {
        CargoNumber cargoNumber = numberRepository.findByNumber(number);
        SimpleNumber simpleNumber = conversionService.convert(cargoNumber, SimpleNumber.class);
        if (simpleNumber == null) {
            throw new RuntimeException("Wrong number");
        }
        return listViewService.makeListElement(simpleNumber);
    }

    private Node getGroupItem(String name) {
        ImageView iv = commonService.getImageView(GROUP_IMAGE, 16);
        Label lb = new Label(name);
        lb.setStyle(String.format("-fx-font-size: %d; -fx-font-weight: bold;", GROUP_LABEL_FONT_SIZE));
        return new HBox(5, iv, lb);
    }
}
