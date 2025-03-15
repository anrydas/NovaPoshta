package das.tools.np.repository.impl;

import das.tools.np.entity.db.ExtraPhone;
import das.tools.np.repository.ExtraPhonesRepository;
import das.tools.np.repository.NumberToPhoneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;

@Repository
@Slf4j
public class ExtraPhonesRepositoryImpl implements ExtraPhonesRepository {
    private static final String SQL_NEW_ORDER_NUM = "select coalesce(max(" + FLD_ORDER_NUMBER + ") + 1, 1) from " + TABLE_NAME;
    private static final String SQL_GET_ALL = "select " + ALL_FIELDS + " from " + TABLE_NAME + " order by " + FLD_ORDER_NUMBER;
    private static final String SQL_GET_DEFAULT_PHONE = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_ORDER_NUMBER + " = 0";
    private static final String SQL_COUNT_DEFAULT = "select count(1) from " + TABLE_NAME + " where " + FLD_ORDER_NUMBER + " = 0";
    private static final String SQL_GET_COUNT = "select count(1) from " + TABLE_NAME + " where " + FLD_PHONE + " = ?";
    private static final String SQL_IS_PHONE_USED = "select count(1) from " + NumberToPhoneRepository.TABLE_NAME + " where " + NumberToPhoneRepository.FLD_NUMBER_ID + " = ?";
    private static final String SQL_GET_BY_ID = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_ID + " = ?";
    private static final String SQL_GET_BY_PHONE = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_PHONE + " = ?";
    private static final String SQL_ADD = "insert into " + TABLE_NAME + "(" + ALL_FIELDS_ADD + ")" + " values (?,?)";
    private static final String SQL_UPDATE = "update " + TABLE_NAME + " set " + FLD_PHONE + " = ?, " + FLD_ORDER_NUMBER + " = ?" + " where " + FLD_ID + " = ?";
    private static final String SQL_DELETE = "delete from " + TABLE_NAME + " where " + FLD_ID + " = ?";

    private final JdbcTemplate jdbcTemplate;

    public ExtraPhonesRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int nextOrderNumber() {
        Integer order_number = jdbcTemplate.queryForObject(SQL_NEW_ORDER_NUM, Integer.class);
        if (order_number == null) {
            throw new RuntimeException("Couldn't get new ID");
        }
        if (log.isDebugEnabled()) log.debug("got new order_number={}", order_number);
        return order_number;
    }

    @Override
    public List<ExtraPhone> getAll() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL);
        List<ExtraPhone> phones = jdbcTemplate.query(SQL_GET_ALL, new PhonesRowMapper<ExtraPhone>());
        if (log.isDebugEnabled()) log.debug("got extra_phones={}", phones);
        return phones;
    }

    @Override
    public List<String> getAllNumbers() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL);
        List<String> phones = jdbcTemplate.query(SQL_GET_ALL, (rs, rowNum) -> rs.getString(FLD_PHONE));
        if (log.isDebugEnabled()) log.debug("got extra_phones={}", phones);
        return phones;
    }

    @Override
    public ExtraPhone findById(long id) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, id={}", SQL_GET_BY_ID, id);
        ExtraPhone phone = jdbcTemplate.queryForObject(SQL_GET_BY_ID, new PhonesRowMapper<ExtraPhone>(), id);
        if (log.isDebugEnabled()) log.debug("got phone={}", phone);
        return phone;
    }

    @Override
    public ExtraPhone getDefault() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_DEFAULT_PHONE);
        ExtraPhone phone = jdbcTemplate.queryForObject(SQL_GET_DEFAULT_PHONE, new PhonesRowMapper<ExtraPhone>());
        if (log.isDebugEnabled()) log.debug("got phone={}", phone);
        return phone;
    }

    @Override
    public boolean isDefaultExists() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_COUNT_DEFAULT);
        Integer cnt = jdbcTemplate.queryForObject(SQL_COUNT_DEFAULT, Integer.class);
        return cnt != null && cnt > 0;
    }

    @Override
    public ExtraPhone setDefault(String phone) {
        if (log.isDebugEnabled()) log.debug("Updating default phone to '{}'", phone);
        ExtraPhone extraPhone;
        if (isPhoneAlreadyExists(phone)) {
            extraPhone = findByPhone(phone);
            extraPhone.setOrderNumber(0);
            return update(extraPhone);
        } else if (isDefaultExists()) {
            extraPhone = getDefault();
            extraPhone.setPhone(phone);
            return update(extraPhone);
        } else {
            extraPhone = ExtraPhone.builder()
                    .phone(phone)
                    .orderNumber(0)
                    .build();
            return add(extraPhone);
        }
    }

    @Override
    public ExtraPhone findByPhone(String phone) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, phone={}", SQL_GET_BY_PHONE, phone);
        ExtraPhone extraPhone = jdbcTemplate.queryForObject(SQL_GET_BY_PHONE, new PhonesRowMapper<ExtraPhone>(), phone);
        if (log.isDebugEnabled()) log.debug("got extraPhone={}", extraPhone);
        return extraPhone;
    }

    @Override
    public ExtraPhone add(ExtraPhone phone) {
        if (!isPhoneAlreadyExists(phone.getPhone())) {
            int orderNumber = nextOrderNumber();
            if (log.isDebugEnabled()) log.debug("Executing SQL={}, phone={}, orderNumber={}", SQL_ADD, phone, orderNumber);
            jdbcTemplate.update(SQL_ADD,
                    phone.getPhone(),
                    orderNumber
            );
        } else {
            log.warn(String.format("Phone '%s' doesn't added: it already exists in DB", phone.getPhone()));
        }
        return findByPhone(phone.getPhone());
    }

    @Override
    public ExtraPhone add(String phone) {
        return add(ExtraPhone.builder().phone(phone).build());
    }

    @Override
    public ExtraPhone update(ExtraPhone phone) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, phone={}", SQL_UPDATE, phone);
        jdbcTemplate.update(SQL_UPDATE, phone.getPhone(), phone.getOrderNumber(), phone.getId());
        return findByPhone(phone.getPhone());
    }

    @Override
    public boolean isPhoneAlreadyExists(String phone) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, phone={}", SQL_GET_COUNT, phone);
        Integer res = jdbcTemplate.queryForObject(SQL_GET_COUNT, Integer.class, phone);
        return (res != null && res > 0);
    }

    @Override
    public boolean isUsedInNumber(ExtraPhone phone) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, phone={}", SQL_IS_PHONE_USED, phone);
        Integer count = jdbcTemplate.queryForObject(SQL_IS_PHONE_USED, Integer.class, phone.getId());
        if (log.isDebugEnabled()) log.debug("got count={}", count);
        return count != null && count > 0;
    }

    @Override
    public boolean isPhoneValid(String phone) {
        Matcher matcher = PHONE_PATTERN.matcher(phone);
        return matcher.find();
    }

    @Override
    public void remove(ExtraPhone phone) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, phone={}", SQL_DELETE, phone);
        ExtraPhone extraPhone = findById(phone.getId());
        if (extraPhone != null) {
            if (!isUsedInNumber(extraPhone)) {
                jdbcTemplate.update(SQL_DELETE, phone.getId());
                if (log.isDebugEnabled()) log.debug("Removed phone={}", phone);
            } else {
                log.warn("The phone '%s' used in number(s). So it couldn't be deleted");
            }
        }
    }

    private static final class PhonesRowMapper<T> implements RowMapper<ExtraPhone> {
        @Override
        public ExtraPhone mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ExtraPhone.builder()
                    .id(rs.getLong(FLD_ID))
                    .phone(rs.getString(FLD_PHONE))
                    .orderNumber(rs.getInt(FLD_ORDER_NUMBER))
                    .build();
        }
    }
}
