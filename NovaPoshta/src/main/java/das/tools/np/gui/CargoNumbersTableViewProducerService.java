package das.tools.np.gui;

import das.tools.np.entity.db.CargoNumber;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
@Slf4j
public class CargoNumbersTableViewProducerService {
    protected static final int COLUMN_MIN_WIDTH_100 = 100;
    protected static final int COLUMN_MIN_WIDTH_200 = 200;
    protected static final int COLUMN_MIN_WIDTH_300 = 300;
    protected static final String NUMBER_FIELD_NAME = "number";
    protected static final String GROUP_NAME_FIELD_NAME = "groupName";
    protected static final String DESCR_FIELD_NAME = "descr";
    protected static final String CREATED_FIELD_NAME = "created";
    protected static final String SENDER_ADDRESS_FIELD_NAME = "senderAddress";
    private final FieldNameService fieldNameService;

    public CargoNumbersTableViewProducerService(FieldNameService fieldNameService) {
        this.fieldNameService = fieldNameService;
    }

    public TableView<CargoNumber> getNewTable() {
        TableView<CargoNumber> tvResults = new TableView<>();
        tvResults.getColumns().clear();
        TableColumn<CargoNumber, String> numberColumn = getColumn(NUMBER_FIELD_NAME, COLUMN_MIN_WIDTH_100);
        tvResults.getColumns().add(numberColumn);
        tvResults.getColumns().add(getColumn(GROUP_NAME_FIELD_NAME, COLUMN_MIN_WIDTH_100));
        tvResults.getColumns().add(getColumn(CREATED_FIELD_NAME, COLUMN_MIN_WIDTH_100));
        tvResults.getColumns().add(getColumn(DESCR_FIELD_NAME, COLUMN_MIN_WIDTH_200));
        tvResults.getColumns().add(getColumn(SENDER_ADDRESS_FIELD_NAME, COLUMN_MIN_WIDTH_300));
        tvResults.getColumns().add(getOrderNumberTableColumn());
        return tvResults;
    }

    public void doColumnAutoSize(TableView<CargoNumber> treeView, TableColumn<CargoNumber, ?> column) {
        TableColumnHeader header = (TableColumnHeader) treeView.lookup(".column-header");
        if (header != null) {
            try {
                Method field = TableColumnHeader.class.getDeclaredMethod("doColumnAutoSize", TableColumnBase.class, Integer.TYPE);
                field.setAccessible(true);
                field.invoke(header, column, -1);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private TableColumn<CargoNumber,String> getColumn(String field, int minWidth) {
        TableColumn<CargoNumber,String> column = new TableColumn<>(fieldNameService.getFieldFullName(field));
        column.setCellValueFactory(new PropertyValueFactory<>(field));
        column.setMinWidth(minWidth);
        return column;
    }

    private TableColumn<CargoNumber,String> getOrderNumberTableColumn() {
        TableColumn<CargoNumber,String> column = new TableColumn<>("#");
        column.setCellValueFactory(new PropertyValueFactory<>(NUMBER_FIELD_NAME));
        column.setCellFactory(new Callback<>() {
            @Override public TableCell<CargoNumber, String> call(TableColumn<CargoNumber, String> cargoNumberStringTableColumn) {
                return new TableCell<>() {
                    @Override protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (this.getTableRow() != null && item != null) {
                            setText(String.valueOf((this.getTableRow().getIndex() + 1)));
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        column.setSortable(false);
        return column;
    }
}
