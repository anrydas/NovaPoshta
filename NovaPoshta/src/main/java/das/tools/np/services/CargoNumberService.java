package das.tools.np.services;

import das.tools.np.entity.db.*;
import das.tools.np.entity.response.AppResponse;
import das.tools.np.gui.enums.FilteringMode;
import javafx.scene.control.TreeItem;
import javafx.scene.text.TextFlow;

import java.text.SimpleDateFormat;
import java.util.List;

public interface CargoNumberService {
    public static final SimpleDateFormat TREE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    CargoNumber addOrUpdate(CargoNumber number, ExtraPhone extraPhone);

    CargoNumber update(CargoNumber number);

    void reloadNumber(SimpleNumber number, List<SimpleNumber> list);

    void updateNumberData(SimpleNumber number, List<SimpleNumber> list, TextFlow txLog);

    void updateNumberData(SimpleNumber number, TreeItem<String> item, TextFlow txLog);

    void storeNumbersData(AppResponse response, NumberType numberType, String comment, String phone, boolean autoUpdate, Group group);

    void updateNumbersData(AppResponse response, NumberType numberType);

    void updateUncompleted(List<SimpleNumber> list, TextFlow txLog, boolean showDialog);

    void moveNumberToGroup(SimpleNumber number, List<SimpleNumber> list, long newGroupId);
    List<CargoNumber> getAllFiltered(FilteringMode filter, Group group);

    List<CargoNumber> getAllFilteredSortedByUpdateDate(FilteringMode filter);

    List<CargoNumber> getAllFilteredSortedByCreateDate(FilteringMode filter);

    List<String> getNumbersUpdateDates(List<CargoNumber> numbers);

    List<String> getNumbersCreateDates(List<CargoNumber> numbers);
}
