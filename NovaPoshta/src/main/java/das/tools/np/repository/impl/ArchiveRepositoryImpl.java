package das.tools.np.repository.impl;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.CargoStatus;
import das.tools.np.entity.db.NumberType;
import das.tools.np.entity.response.ResponseData;
import das.tools.np.repository.ArchiveRepository;
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
public class ArchiveRepositoryImpl implements ArchiveRepository {
    private static final String SQL_GET_ALL = "select " + ALL_FIELDS + " from " + TABLE_NAME;
    private static final String SQL_GET_BY_NUMBER = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NUMBER + " = ?";
    private static final String SQL_IS_NUMBER_EXISTS = "select count(1) from " + TABLE_NAME + " where " + FLD_NUMBER + " = ?";
    private static final String SQL_ADD = "insert into " + TABLE_NAME + "(" + ALL_FIELDS_ADD + ")" + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_DELETE = "delete from " + TABLE_NAME + " where " + FLD_NUMBER + " = ?";

    private final JdbcTemplate jdbcTemplate;
    private final GroupRepository groupRepository;
    private final ConversionService conversionService;

    public ArchiveRepositoryImpl(JdbcTemplate jdbcTemplate, GroupRepository groupRepository, ConversionService conversionService) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupRepository = groupRepository;
        this.conversionService = conversionService;
    }

    @Override
    public boolean isNumberExists(String number) {
        if (log.isDebugEnabled()) log.debug("ARCHIVE: Executing SQL={}, number={}", SQL_IS_NUMBER_EXISTS, number);
        Integer cnt = jdbcTemplate.queryForObject(SQL_IS_NUMBER_EXISTS, Integer.class, number);
        if (log.isDebugEnabled()) log.debug("ARCHIVE: got cnt={}", cnt);
        return cnt != null && cnt > 0;
    }

    @Override
    public CargoNumber findByNumber(String number) {
        if (log.isDebugEnabled()) log.debug("ARCHIVE: Executing SQL={}, number={}", SQL_GET_BY_NUMBER, number);
        CargoNumber res = null;
        if (isNumberExists(number)) {
            res = jdbcTemplate.queryForObject(SQL_GET_BY_NUMBER, new CargoNumberMapper<CargoNumber>(), number);
            if (log.isDebugEnabled()) log.debug("ARCHIVE: found number={}", res);
        }
        return res;
    }

    @Override
    public List<CargoNumber> findAll() {
        if (log.isDebugEnabled()) log.debug("ARCHIVE: Executing SQL={}", SQL_GET_ALL);
        List<CargoNumber> numbers = jdbcTemplate.query(SQL_GET_ALL, new CargoNumberMapper<CargoNumber>());
        if (log.isDebugEnabled()) log.debug("ARCHIVE: got numbers={}", numbers);
        return numbers;
    }

    @Override
    public CargoNumber add(CargoNumber number) {
        if (log.isDebugEnabled()) log.debug("ARCHIVE: Executing SQL={}, number={}", SQL_ADD, number);
        jdbcTemplate.update(SQL_ADD,
                number.getId(),
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
                conversionService.convert(number.getFullData(), String.class),
                new Date(),
                new Date()
        );
        return number;
    }

    @Override
    public int delete(CargoNumber number) {
        if (log.isDebugEnabled()) log.debug("ARCHIVE: Executing SQL={}, number={}", SQL_DELETE, number);
        int rows = jdbcTemplate.update(SQL_DELETE, number.getNumber());
        if (log.isDebugEnabled()) log.debug("ARCHIVE: removed {} rows", rows);
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
                    .fullData(conversionService.convert(rs.getString(FLD_JSON_DATA), ResponseData.class))
                    .created(rs.getDate(FLD_CREATED))
                    .updated(rs.getDate(FLD_UPDATED))
                    .groupName(rs.getString(FLD_S_GROUP_NAME))
                    .descr(rs.getString(FLD_S_DESCR))
                    .build();
        }
    }
}
