package das.tools.np.repository.impl;

import das.tools.np.entity.db.NumberToPhone;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.repository.ExtraPhonesRepository;
import das.tools.np.repository.NumberToPhoneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@Repository
@Slf4j
public class NumberToPhoneRepositoryImpl implements NumberToPhoneRepository {
    private static final String SQL_NEW_ID = "select coalesce(max(seq), 0) + 1 from sqlite_sequence WHERE name = '" + TABLE_NAME + "'";
    private static final String SQL_GET_BY_ID = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_ID + " = ?";
    private static final String SQL_FIND_RECORD = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NUMBER_ID + " = ? and " + FLD_PHONE_ID + " = ?";
    private static final String SQL_IS_RECORD_EXISTS = "select count(1) from " + TABLE_NAME + " where " + FLD_NUMBER_ID + " = ? and " + FLD_PHONE_ID + " = ?";
    private static final String SQL_ADD = "insert into " + TABLE_NAME + "(" + ALL_FIELDS + ")values(?,?,?)";
    private static final String SQL_GET_FOR_NUMBER =
            "select r." +  FLD_ID + ", r." + FLD_NUMBER_ID + ", r." + FLD_PHONE_ID + ", " + VFLD_NUMBER + ", " + VFLD_PHONE +
            " from " + TABLE_NAME + " r," +
            CargoNumberRepository.TABLE_NAME + " n," +
            ExtraPhonesRepository.TABLE_NAME + " p" +
            " where " + FLD_NUMBER_ID + " =?" +
            " and n.id = r.number_id" +
            " and p.id = r.phone_id" +
            " order by " + FLD_NUMBER_ID + ", " + FLD_PHONE_ID;

    private final JdbcTemplate jdbcTemplate;
    private final CargoNumberRepository numberRepository;
    private final ExtraPhonesRepository phonesRepository;

    public NumberToPhoneRepositoryImpl(JdbcTemplate jdbcTemplate, CargoNumberRepository numberRepository, ExtraPhonesRepository phonesRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.numberRepository = numberRepository;
        this.phonesRepository = phonesRepository;
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
    public NumberToPhone findById(long id) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, id={}", SQL_GET_BY_ID, id);
        NumberToPhone record = jdbcTemplate.queryForObject(SQL_GET_BY_ID, new NumberToPhoneMapper<NumberToPhone>(), id);
        if (log.isDebugEnabled()) log.debug("got record={}", record);
        return record;
    }

    @Override
    public NumberToPhone findForNumber(long numberId) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_FOR_NUMBER);
        NumberToPhone record = jdbcTemplate.queryForObject(SQL_GET_FOR_NUMBER, new NumberToPhoneMapper<NumberToPhone>(), numberId);
        if (log.isDebugEnabled()) log.debug("got record={}", record);
        return record;
    }

    @Override
    public NumberToPhone findForNumber(String number) {
        return findForNumber(numberRepository.findByNumber(number).getId());
    }

    private NumberToPhone findForRecord(long numberId, long phoneId) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_FIND_RECORD);
        NumberToPhone record = jdbcTemplate.queryForObject(SQL_FIND_RECORD, NumberToPhone.class, numberId, phoneId);
        if (log.isDebugEnabled()) log.debug("got record={}", record);
        return record;
    }

    private boolean isExists(long numberId, long phoneId) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, numberId={}, phoneId={}", SQL_IS_RECORD_EXISTS, numberId, phoneId);
        Integer count = jdbcTemplate.queryForObject(SQL_IS_RECORD_EXISTS, Integer.class, numberId, phoneId);
        if (log.isDebugEnabled()) log.debug("got count={}", count);
        return count != null && count > 0;
    }

    private NumberToPhone add(NumberToPhone record) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, record={}", SQL_ADD, record);
        jdbcTemplate.update(SQL_ADD,
                record.getId(),
                record.getNumberId(),
                record.getPhoneId());
        return findById(record.getId());
    }

    @Override
    public NumberToPhone add(String number, String phone) {
        long numberId = numberRepository.findByNumber(number).getId();
        long phoneId = phonesRepository.findByPhone(phone).getId();
        if (isExists(numberId, phoneId)) {
            NumberToPhone record = findForRecord(numberId, phoneId);
            log.warn("Record 'number={}, phone={} (number_id={}, phone_id={})' already exists", number, phone, numberId, phoneId);
            return record;
        }
        long id = nextId();
        return add(NumberToPhone.builder()
                .id(id)
                .numberId(numberId)
                .phoneId(phoneId)
                .build()
        );
    }

    private static final class NumberToPhoneMapper<T> implements RowMapper<NumberToPhone> {
        @Override
        public NumberToPhone mapRow(ResultSet rs, int rowNum) throws SQLException {
            return NumberToPhone.builder()
                    .id(rs.getLong(FLD_ID))
                    .numberId(rs.getLong(FLD_NUMBER_ID))
                    .phoneId(rs.getLong(FLD_PHONE_ID))
                    .number(hasColumn(rs, VFLD_NUMBER) ? rs.getString(VFLD_NUMBER) : "")
                    .phone(hasColumn(rs, VFLD_PHONE) ? rs.getString(VFLD_PHONE) : "")
                    .build();
        }
        private static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            for (int x = 1; x <= columns; x++) {
                if (columnName.equals(rsmd.getColumnName(x))) {
                    return true;
                }
            }
            return false;
        }
    }
}
