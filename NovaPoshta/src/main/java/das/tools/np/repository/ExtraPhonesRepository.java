package das.tools.np.repository;

import das.tools.np.entity.db.ExtraPhone;

import java.util.List;
import java.util.regex.Pattern;

public interface ExtraPhonesRepository {
    Pattern PHONE_PATTERN = Pattern.compile("^(\\+)?(\\d{2})?\\d{10}$");
    String TABLE_NAME = "extra_phones";
    String FLD_ID = "id";
    String FLD_PHONE = "phone";
    String FLD_ORDER_NUMBER = "order_number";
    String ALL_FIELDS = FLD_ID + ", " +
            FLD_PHONE + ", " +
            FLD_ORDER_NUMBER;
    String ALL_FIELDS_ADD = FLD_PHONE + ", " +
            FLD_ORDER_NUMBER;

    int nextOrderNumber();

    List<ExtraPhone> getAll();
    List<String> getAllNumbers();

    ExtraPhone findById(long id);

    ExtraPhone getDefault();

    boolean isDefaultExists();

    ExtraPhone setDefault(String phone);

    ExtraPhone findByPhone(String phone);

    ExtraPhone add(ExtraPhone phone);

    ExtraPhone add(String phone);

    ExtraPhone update(ExtraPhone phone);

    boolean isPhoneAlreadyExists(String phone);

    boolean isUsedInNumber(ExtraPhone phone);

    boolean isPhoneValid(String phone);

    void remove(ExtraPhone phone);
}
