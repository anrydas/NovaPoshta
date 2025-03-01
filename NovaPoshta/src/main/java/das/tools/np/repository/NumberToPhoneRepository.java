package das.tools.np.repository;

import das.tools.np.entity.db.NumberToPhone;

public interface NumberToPhoneRepository {
    String TABLE_NAME = "number_to_phone";
    String FLD_ID = "id";
    String FLD_NUMBER_ID = "number_id";
    String FLD_PHONE_ID = "phone_id";
    String VFLD_NUMBER = "number";
    String VFLD_PHONE = "phone";
    String ALL_FIELDS =
            FLD_ID + ", " +
            FLD_NUMBER_ID + ", " +
            FLD_PHONE_ID;

    long nextId();

    NumberToPhone findById(long id);

    NumberToPhone findForNumber(long numberId);

    NumberToPhone findForNumber(String number);

    NumberToPhone add(String number, String phone);
}
