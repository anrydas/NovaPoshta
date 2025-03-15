package das.tools.np.repository.impl;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.CargoStatus;
import das.tools.np.entity.db.Group;
import das.tools.np.entity.db.NumberType;
import das.tools.np.entity.response.ResponseData;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class CargoNumberRepositoryImpl implements CargoNumberRepository {
    private static final String SQL_NEW_ID = "select coalesce(max(seq), 0) + 1 from sqlite_sequence WHERE name = '" + TABLE_NAME + "'";
    private static final String SQL_GET_BY_ID = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_ID + " = ?";
    private static final String SQL_GET_ALL = "select " + ALL_FIELDS + " from " + TABLE_NAME;
    private static final String SQL_GET_UNCOMPLETED = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_APP_STATUS + " != " + CargoStatus.COMPLETED.ordinal();
    private static final String SQL_GET_UNCOMPLETED_AUTO = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_APP_STATUS + " != " + CargoStatus.COMPLETED.ordinal() + " and " + FLD_AUTO_UPDATED + " = 1";
    private static final String SQL_GET_UNCOMPLETED_IN_GROUP = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_APP_STATUS + " != " + CargoStatus.COMPLETED.ordinal() + " and " + FLD_GROUP + " = ?";
    private static final String SQL_GET_ALL_BY_UPDATE_DATE = "select " + ALL_FIELDS + " from " + TABLE_NAME + " order by " + FLD_UPDATED + " desc";
    private static final String SQL_GET_ALL_BY_CREATE_DATE = "select " + ALL_FIELDS + " from " + TABLE_NAME + " order by " + FLD_CREATED + " desc";
    private static final String SQL_GET_UNCOMPLETED_BY_UPDATE_DATE = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_APP_STATUS + " != " + CargoStatus.COMPLETED.ordinal() + " order by " + FLD_UPDATED + " desc";
    private static final String SQL_GET_UNCOMPLETED_BY_CREATE_DATE = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_APP_STATUS + " != " + CargoStatus.COMPLETED.ordinal() + " order by " + FLD_CREATED + " desc";
    private static final String SQL_GET_BY_TYPE = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NUMBER_TYPE + " = ?";
    private static final String SQL_GET_BY_TYPE_IN_GROUP = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NUMBER_TYPE + " = ? and " + FLD_GROUP + " = ?";
    private static final String SQL_GET_BY_TYPE_BY_UPDATE_DATE = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NUMBER_TYPE + " = ? " + " order by " + FLD_UPDATED + " desc";
    private static final String SQL_GET_BY_TYPE_BY_CREATE_DATE = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NUMBER_TYPE + " = ? " + " order by " + FLD_CREATED + " desc";
    private static final String SQL_GET_BY_NUMBER = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NUMBER + " = ?";
    private static final String SQL_IS_NUMBER_EXISTS = "select count(1) from " + TABLE_NAME + " where " + FLD_NUMBER + " = ?";
    private static final String SQL_COUNT_IN_GROUP = "select count(1) as cnt from " + TABLE_NAME + " where " + FLD_GROUP + " = ?";
    private static final String SQL_ADD = "insert into " + TABLE_NAME + "(" + ALL_FIELDS_ADD + ")" + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "update " + TABLE_NAME + " set " + FLD_NUMBER + " = ?,"+ FLD_GROUP + " = ?,"+
                    FLD_APP_STATUS + " = ?,"+FLD_COMMENT + " = ?,"+ FLD_DATE_CREATED + " = ?,"+ FLD_NUMBER_TYPE + " = ?,"+ FLD_WEIGHT + " = ?,"+ FLD_COST + " = ?,"+
                    FLD_DESCR + " = ?,"+ FLD_CARGO_TYPE + " = ?,"+ FLD_STATUS + " = ?,"+ FLD_SEATS + " = ?,"+
                    FLD_ANNOUNCED_PRICE + " = ?,"+ FLD_DELIVERY_SCHED_DATE + " = ?,"+ FLD_RECIP_FULL_NAME + " = ?,"+ FLD_CITY_RECIP + " = ?,"+
                    FLD_WAREHOUSE_RECIP + " = ?,"+ FLD_WAREHOUSE_RECIP_NUMBER + " = ?,"+ FLD_PHONE_RECIP + " = ?,"+ FLD_ADDR_RECIP + " = ?,"+
                    FLD_CITY_SENDER + " = ?,"+ FLD_PHONE_SENDER + " = ?,"+ FLD_WAREHOUSE_SENDER + " = ?,"+ FLD_ADDR_SENDER + " = ?,"+ FLD_AUTO_UPDATED + " = ?,"+
                    FLD_JSON_DATA + " = ?," + "updated = ? " +
            "where " + FLD_ID + " = ?";
    private static final String SQL_DELETE = "delete from " + TABLE_NAME + " where " + FLD_ID + " = ?";
    private static final String SQL_MOVE_TO_GROUP = "update " + TABLE_NAME + " set " + FLD_GROUP + " = ?" + " where " + FLD_ID + " = ?";
    private static final String SQL_GET_ALL_BY_GROUP = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_GROUP + " = ?";

    private final JdbcTemplate jdbcTemplate;
    private final GroupRepository groupRepository;
    private final ConversionService conversionService;

    public CargoNumberRepositoryImpl(JdbcTemplate jdbcTemplate, GroupRepository groupRepository, ConversionService conversionService) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupRepository = groupRepository;
        this.conversionService = conversionService;
    }

    @Override
    public long nextId() {
        Long id = jdbcTemplate.queryForObject(SQL_NEW_ID, Long.class);
        if (id == null) {
            throw new RuntimeException("Couldn't get new ID");
        }
        if (log.isDebugEnabled()) log.debug("got new id={}", id);
        return id;
    }

    @Override
    public CargoNumber findById(long id) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, id={}", SQL_GET_BY_ID, id);
        CargoNumber number = jdbcTemplate.queryForObject(SQL_GET_BY_ID, new CargoNumberMapper<CargoNumber>(), id);
        if (log.isDebugEnabled()) log.debug("got number={}", number);
        return number;
    }

    @Override
    public CargoNumber findByNumber(String number) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, number={}", SQL_GET_BY_NUMBER, number);
        CargoNumber res = null;
        if (isNumberExists(number)) {
            res = jdbcTemplate.queryForObject(SQL_GET_BY_NUMBER, new CargoNumberMapper<CargoNumber>(), number);
            if (log.isDebugEnabled()) log.debug("found number={}", res);
        }
        return res;
    }

    @Override
    public List<CargoNumber> findAll() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_ALL, new CargoNumberMapper<CargoNumber>());
        if (log.isDebugEnabled()) log.debug("got numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findAllByGroup(Group group) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL_BY_GROUP);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_ALL_BY_GROUP, new CargoNumberMapper<CargoNumber>(), group.getId());
        if (log.isDebugEnabled()) log.debug("got numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findAllByType(NumberType type) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, type={}", SQL_GET_BY_TYPE, type.ordinal());
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_BY_TYPE, new CargoNumberMapper<CargoNumber>(), type.ordinal());
        if (log.isDebugEnabled()) log.debug("got numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findUncompleted() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_UNCOMPLETED);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_UNCOMPLETED, new CargoNumberMapper<CargoNumber>());
        if (log.isDebugEnabled()) log.debug("got uncompleted numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findUncompletedAutoUpdated() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_UNCOMPLETED_AUTO);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_UNCOMPLETED_AUTO, new CargoNumberMapper<CargoNumber>());
        if (log.isDebugEnabled()) log.debug("got uncompleted auto updated numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findAllByType(NumberType type, Group group) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, groupId={}", SQL_GET_BY_TYPE_IN_GROUP, group.getId());
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_BY_TYPE_IN_GROUP, new CargoNumberMapper<CargoNumber>(), type.ordinal(), group.getId());
        if (log.isDebugEnabled()) log.debug("got numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findUncompleted(Group group) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, groupId={}", SQL_GET_UNCOMPLETED_IN_GROUP, group.getId());
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_UNCOMPLETED_IN_GROUP, new CargoNumberMapper<CargoNumber>(), group.getId());
        if (log.isDebugEnabled()) log.debug("got uncompleted numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findAllSortedByUpdateDate() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL_BY_UPDATE_DATE);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_ALL_BY_UPDATE_DATE, new CargoNumberMapper<CargoNumber>());
        if (log.isDebugEnabled()) log.debug("got all sorted by update date numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findAllByTypeSortedByUpdateDate(NumberType type) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, type={}", SQL_GET_BY_TYPE_BY_UPDATE_DATE, type);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_BY_TYPE_BY_UPDATE_DATE, new CargoNumberMapper<CargoNumber>(), type.ordinal());
        if (log.isDebugEnabled()) log.debug("got all by type sorted by update date numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findUncompletedSortedByUpdateDate() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_UNCOMPLETED_BY_UPDATE_DATE);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_UNCOMPLETED_BY_UPDATE_DATE, new CargoNumberMapper<CargoNumber>());
        if (log.isDebugEnabled()) log.debug("got uncompleted by update date numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findAllSortedByCreateDate() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL_BY_CREATE_DATE);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_ALL_BY_CREATE_DATE, new CargoNumberMapper<CargoNumber>());
        if (log.isDebugEnabled()) log.debug("got all sorted by create date numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findAllByTypeSortedByCreateDate(NumberType type) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, type={}", SQL_GET_BY_TYPE_BY_CREATE_DATE, type);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_BY_TYPE_BY_CREATE_DATE, new CargoNumberMapper<CargoNumber>(), type.ordinal());
        if (log.isDebugEnabled()) log.debug("got all by type sorted by create date numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> findUncompletedSortedByCreateDate() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_UNCOMPLETED_BY_CREATE_DATE);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_UNCOMPLETED_BY_CREATE_DATE, new CargoNumberMapper<CargoNumber>());
        if (log.isDebugEnabled()) log.debug("got uncompleted by create date numbers={}", numbers);
        return numbers;
    }

    @Override
    public boolean isNumberExists(String number) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, number={}", SQL_IS_NUMBER_EXISTS, number);
        Integer cnt = jdbcTemplate.queryForObject(SQL_IS_NUMBER_EXISTS, Integer.class, number);
        if (log.isDebugEnabled()) log.debug("got cnt={}", cnt);
        return cnt != null && cnt > 0;
    }

    @Override
    public int numbersInGroup(long groupId) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, groupId={}", SQL_COUNT_IN_GROUP, groupId);
        Integer res = jdbcTemplate.queryForObject(SQL_COUNT_IN_GROUP, Integer.class, groupId);
        if (log.isDebugEnabled()) log.debug("got count={}", res);
        return res != null ? res : 0;
    }

    @Override
    public CargoNumber add(CargoNumber number) {
        long id = nextId();
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, number={}, id={}", SQL_ADD, number, id);
        jdbcTemplate.update(SQL_ADD,
                id,
                number.getNumber(),
                number.getGroup() != null ? number.getGroup().getId() : 1,
                number.getAppStatus().ordinal(),
                number.getComment(),
                number.getNumberType().ordinal(),
                number.getDateCreated(),
                number.getWeight(),
                number.getCost(),
                number.getDescription(),
                number.getCargoType(),
                number.getStatusCode(),
                number.getSeatsAmount(),
                number.getAnnouncedPrice(),
                number.getScheduledDeliveryDate(),
                number.getRecipientFullName(),
                number.getCityRecipient(),
                number.getWarehouseRecipient(),
                number.getWarehouseRecipientNumber(),
                number.getPhoneRecipient(),
                number.getRecipientAddress(),
                number.getCitySender(),
                number.getPhoneSender(),
                number.getWarehouseSender(),
                number.getSenderAddress(),
                number.isAutoUpdated(),
                conversionService.convert(number.getFullData(), String.class),
                new Date(),
                new Date()
        );
        return findById(id);
    }

    @Override
    public CargoNumber update(CargoNumber number) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, number={}", SQL_UPDATE, number);
        jdbcTemplate.update(SQL_UPDATE,
                number.getNumber(),
                number.getGroup().getId(),
                number.getAppStatus().ordinal(),
                number.getComment(),
                number.getDateCreated(),
                number.getNumberType().ordinal(),
                number.getWeight(),
                number.getCost(),
                number.getDescription(),
                number.getCargoType(),
                number.getStatusCode(),
                number.getSeatsAmount(),
                number.getAnnouncedPrice(),
                number.getScheduledDeliveryDate(),
                number.getRecipientFullName(),
                number.getCityRecipient(),
                number.getWarehouseRecipient(),
                number.getWarehouseRecipientNumber(),
                number.getPhoneRecipient(),
                number.getRecipientAddress(),
                number.getCitySender(),
                number.getPhoneSender(),
                number.getWarehouseSender(),
                number.getSenderAddress(),
                number.isAutoUpdated(),
                conversionService.convert(number.getFullData(), String.class),
                new Date(),
                number.getId()
        );
        return findById(number.getId());
    }

    @Override
    public CargoNumber moveToGroup(CargoNumber number, Group newGroup) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, number={}, group={}", SQL_MOVE_TO_GROUP, number.getNumber(), newGroup);
        jdbcTemplate.update(SQL_MOVE_TO_GROUP, newGroup.getId(), number.getId());
        return findById(number.getId());
    }


    @Override
    public int delete(CargoNumber number) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, number={}", SQL_DELETE, number);
        int rows = jdbcTemplate.update(SQL_DELETE, number.getId());
        if (log.isDebugEnabled()) log.debug("removed {} rows", rows);
        return rows;
    }

    private final class CargoNumberMapper<T> implements RowMapper<CargoNumber> {
        @Override
        public CargoNumber mapRow(ResultSet rs, int rowNum) throws SQLException {
            return CargoNumber.builder()
                    .id(rs.getLong(FLD_ID))
                    .number(rs.getString(FLD_NUMBER))
                    .group(groupRepository.findById(rs.getLong(FLD_GROUP)))
                    .appStatus(CargoStatus.valueOf(rs.getInt(FLD_APP_STATUS)))
                    .comment(rs.getString(FLD_COMMENT))
                    .numberType(NumberType.valueOf(rs.getInt(FLD_NUMBER_TYPE)))
                    .dateCreated(rs.getString(FLD_DATE_CREATED))
                    .weight(rs.getFloat(FLD_WEIGHT))
                    .cost(rs.getFloat(FLD_COST))
                    .seatsAmount(rs.getFloat(FLD_SEATS))
                    .description(rs.getString(FLD_DESCR))
                    .cargoType(rs.getString(FLD_CARGO_TYPE))
                    .statusCode(rs.getInt(FLD_STATUS))
                    .announcedPrice(rs.getString(FLD_ANNOUNCED_PRICE))
                    .scheduledDeliveryDate(rs.getString(FLD_DELIVERY_SCHED_DATE))
                    .recipientFullName(rs.getString(FLD_RECIP_FULL_NAME))
                    .cityRecipient(rs.getString(FLD_CITY_RECIP))
                    .warehouseRecipient(rs.getString(FLD_WAREHOUSE_RECIP))
                    .warehouseRecipientNumber(rs.getString(FLD_WAREHOUSE_RECIP_NUMBER))
                    .phoneRecipient(rs.getString(FLD_PHONE_RECIP))
                    .recipientAddress(rs.getString(FLD_ADDR_RECIP))
                    .citySender(rs.getString(FLD_CITY_SENDER))
                    .phoneSender(rs.getString(FLD_PHONE_SENDER))
                    .warehouseSender(rs.getString(FLD_WAREHOUSE_SENDER))
                    .senderAddress(rs.getString(FLD_ADDR_SENDER))
                    .autoUpdated(rs.getBoolean(FLD_AUTO_UPDATED))
                    .fullData(conversionService.convert(rs.getString(FLD_JSON_DATA), ResponseData.class))
                    .created(rs.getDate(FLD_CREATED))
                    .updated(rs.getDate(FLD_UPDATED))
                    .groupName(rs.getString(FLD_S_GROUP_NAME))
                    .descr(rs.getString(FLD_S_DESCR))
                    .build();
        }
    }
}
