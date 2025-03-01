package das.tools.np.repository;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.Group;
import das.tools.np.entity.db.NumberType;

import java.util.List;

public interface CargoNumberRepository {
    String TABLE_NAME = "numbers";
    String FLD_ID = "id";
    String FLD_NUMBER = "number";
    String FLD_GROUP = "group_id";
    String FLD_APP_STATUS = "app_status";
    String FLD_COMMENT = "comment";
    String FLD_NUMBER_TYPE = "number_type";
    String FLD_DATE_CREATED = "dateCreated";
    String FLD_WEIGHT = "weight";
    String FLD_COST = "cost";
    String FLD_DESCR = "description";
    String FLD_CARGO_TYPE = "cargoType";
    String FLD_STATUS = "status";
    String FLD_SEATS = "seatsAmount";
    String FLD_ANNOUNCED_PRICE = "announcedPrice";
    String FLD_DELIVERY_SCHED_DATE = "scheduledDeliveryDate";
    String FLD_RECIP_FULL_NAME = "recipientFullName";
    String FLD_CITY_RECIP = "cityRecipient";
    String FLD_WAREHOUSE_RECIP = "warehouseRecipient";
    String FLD_WAREHOUSE_RECIP_NUMBER = "warehouseRecipientNumber";
    String FLD_PHONE_RECIP = "phoneRecipient";
    String FLD_ADDR_RECIP = "recipientAddress";
    String FLD_CITY_SENDER = "citySender";
    String FLD_PHONE_SENDER = "phoneSender";
    String FLD_WAREHOUSE_SENDER = "warehouseSender";
    String FLD_ADDR_SENDER = "senderAddress";
    String FLD_AUTO_UPDATED = "autoUpdated";
    String FLD_JSON_DATA = "json_data";
    String FLD_UPDATED = "updated";
    String FLD_CREATED = "created";
    String FLD_S_DESCR = "descr";
    String FLD_S_GROUP_NAME = "groupName";
    String ALL_FIELDS =
            FLD_ID + ", " +
            FLD_NUMBER + ", " +
            FLD_GROUP + ", " +
            FLD_APP_STATUS + ", " +
            FLD_COMMENT + ", " +
            FLD_NUMBER_TYPE + ", " +
            FLD_DATE_CREATED + ", " +
            FLD_WEIGHT + ", " +
            FLD_COST + ", " +
            FLD_DESCR + ", " +
            FLD_CARGO_TYPE + ", " +
            FLD_STATUS + ", " +
            FLD_SEATS + ", " +
            FLD_ANNOUNCED_PRICE + ", " +
            FLD_DELIVERY_SCHED_DATE + ", " +
            FLD_RECIP_FULL_NAME + ", " +
            FLD_CITY_RECIP + ", " +
            FLD_WAREHOUSE_RECIP + ", " +
            FLD_WAREHOUSE_RECIP_NUMBER + ", " +
            FLD_PHONE_RECIP + ", " +
            FLD_ADDR_RECIP + ", " +
            FLD_CITY_SENDER + ", " +
            FLD_PHONE_SENDER + ", " +
            FLD_WAREHOUSE_SENDER + ", " +
            FLD_ADDR_SENDER + ", " +
            FLD_JSON_DATA + ", " +
            FLD_CREATED + ", " +
            FLD_UPDATED + ", " +
            FLD_AUTO_UPDATED + ", " +
            "coalesce("+FLD_COMMENT+", "+FLD_DESCR+") as " + FLD_S_DESCR + ", " +
            "(select name from groups g where g.id = "+FLD_GROUP+") as " + FLD_S_GROUP_NAME;
    String ALL_FIELDS_ADD =
            FLD_ID + ", " +
            FLD_NUMBER + ", " +
            FLD_GROUP + ", " +
            FLD_APP_STATUS + ", " +
            FLD_COMMENT + ", " +
            FLD_NUMBER_TYPE + ", " +
            FLD_DATE_CREATED + ", " +
            FLD_WEIGHT + ", " +
            FLD_COST + ", " +
            FLD_DESCR + ", " +
            FLD_CARGO_TYPE + ", " +
            FLD_STATUS + ", " +
            FLD_SEATS + ", " +
            FLD_ANNOUNCED_PRICE + ", " +
            FLD_DELIVERY_SCHED_DATE + ", " +
            FLD_RECIP_FULL_NAME + ", " +
            FLD_CITY_RECIP + ", " +
            FLD_WAREHOUSE_RECIP + ", " +
            FLD_WAREHOUSE_RECIP_NUMBER + ", " +
            FLD_PHONE_RECIP + ", " +
            FLD_ADDR_RECIP + ", " +
            FLD_CITY_SENDER + ", " +
            FLD_PHONE_SENDER + ", " +
            FLD_WAREHOUSE_SENDER + ", " +
            FLD_ADDR_SENDER + ", " +
            FLD_AUTO_UPDATED + ", " +
            FLD_JSON_DATA + ", " +
            FLD_CREATED + ", " +
            FLD_UPDATED;

    long nextId();

    CargoNumber findById(long id);

    CargoNumber findByNumber(String number);

    List<CargoNumber> findAll();

    List<CargoNumber> findAllByGroup(Group group);

    List<CargoNumber> findAllByType(NumberType type);

    List<CargoNumber> findUncompleted();

    List<CargoNumber> findUncompletedAutoUpdated();

    List<CargoNumber> findAllByType(NumberType type, Group group);

    List<CargoNumber> findUncompleted(Group group);

    List<CargoNumber> findAllSortedByUpdateDate();

    List<CargoNumber> findAllByTypeSortedByUpdateDate(NumberType type);

    List<CargoNumber> findUncompletedSortedByUpdateDate();

    List<CargoNumber> findAllSortedByCreateDate();

    List<CargoNumber> findAllByTypeSortedByCreateDate(NumberType type);

    List<CargoNumber> findUncompletedSortedByCreateDate();

    boolean isNumberExists(String number);

    int numbersInGroup(long groupId);

    CargoNumber add(CargoNumber number);

    CargoNumber update(CargoNumber number);

    CargoNumber moveToGroup(CargoNumber number, Group newGroup);

    int delete(CargoNumber number);
}
