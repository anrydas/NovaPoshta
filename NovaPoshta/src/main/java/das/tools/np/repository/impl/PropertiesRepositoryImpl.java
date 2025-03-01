package das.tools.np.repository.impl;

import das.tools.np.entity.db.Property;
import das.tools.np.repository.PropertiesRepository;
import das.tools.np.services.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Repository
@Slf4j
public class PropertiesRepositoryImpl implements PropertiesRepository {
    private static final String SQL_NEW_ID = "select coalesce(max(seq), 0) + 1 from sqlite_sequence WHERE name = '" + TABLE_NAME + "'";
    private static final String SQL_GET_ALL = "select " + ALL_FIELDS + " from " + TABLE_NAME;
    //private static final String SQL_GET_ALL = "select " + ALL_FIELDS + " from (select p.*, row_number() over (partition by prop_key order by id) as rn from " + TABLE_NAME + " p) where rn = 1";
    private static final String SQL_GET_PROPERTY_VALUE = "select " + FLD_VALUE + " from " + TABLE_NAME + " where " + FLD_KEY + " = ?";
    private static final String SQL_GET_BY_ID = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_ID + " = ?";
    private static final String SQL_IS_PROPERTY_EXISTS = "select count(1) cnt from " + TABLE_NAME + " where " + FLD_KEY + " = ?";
    private static final String SQL_ADD_PHONE = "insert into " + TABLE_NAME + "(" + FLD_KEY + "," + FLD_VALUE + ")values('" +
            ConfigService.CONFIG_PHONE_NUMBER_KEY + "', ?" + ")";
    private static final String SQL_UPDATE_PROPERTY =
            "update " + TABLE_NAME + " set " +
            FLD_VALUE + " = ?, " +
            "updated = CURRENT_TIMESTAMP " +
            "where " + FLD_KEY + " = ?";
    private static final String SQL_ADD_PROPERTY =
            "insert into " + TABLE_NAME + "(" + ALL_FIELDS + ", updated)values(?,?,?,CURRENT_TIMESTAMP)";

    private final JdbcTemplate jdbcTemplate;

    public PropertiesRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    public List<Property> getAll() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL);
        List<Property> res = jdbcTemplate.query(SQL_GET_ALL, new PropertiesRowMapper<Property>());
        if (log.isDebugEnabled()) log.debug("got props={}", res);
        return res;
    }

    @Override
    public Property findById(long id) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={} for id={}", SQL_GET_BY_ID, id);
        Property res = jdbcTemplate.queryForObject(SQL_GET_BY_ID, new PropertiesRowMapper<Property>(), id);
        if (log.isDebugEnabled()) log.debug("got prop={}", res);
        return res;
    }

    @Override @Deprecated
    public String getPhone() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_PROPERTY_VALUE);
        String res = jdbcTemplate.queryForObject(
                SQL_GET_PROPERTY_VALUE,
                String.class,
                ConfigService.CONFIG_PHONE_NUMBER_KEY);
        if (log.isDebugEnabled()) log.debug("got phone={}", res);
        return res;
    }

    @Override
    public List<Property> update(String key, String value) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_UPDATE_PROPERTY);
        jdbcTemplate.update(SQL_UPDATE_PROPERTY, value, key);
        return getAll();
    }

    @Override
    public Property add(String key, String value) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={} for key={}, value={}", SQL_ADD_PROPERTY, key, value);
        long id = nextId();
        jdbcTemplate.update(SQL_ADD_PROPERTY, id, key, value);
        return findById(id);
    }

    @Override
    public boolean isPropertyExists(String key) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={} for key={}", SQL_ADD_PROPERTY, key);
        Integer cnt = jdbcTemplate.queryForObject(SQL_IS_PROPERTY_EXISTS, Integer.class, key);
        return cnt > 0;
    }

    @Override
    public List<Property> updateAll(Properties props) {
        props.forEach((k, v) -> {
            if (isPropertyExists((String) k)) {
                update((String) k, (String) v);
            } else {
                add((String) k, (String) v);
            }
        });
        return getAll();
    }

    private static final class PropertiesRowMapper<T> implements RowMapper<Property> {
        @Override
        public Property mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Property.builder()
                    .key(rs.getString(FLD_KEY))
                    .value(rs.getString(FLD_VALUE))
                    .build();
        }
    }
}

