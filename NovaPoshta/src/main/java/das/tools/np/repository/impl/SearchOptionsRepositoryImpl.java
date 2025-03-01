package das.tools.np.repository.impl;

import das.tools.np.entity.db.SearchOptions;
import das.tools.np.entity.search.SearchParams;
import das.tools.np.repository.SearchOptionsRepository;
import das.tools.np.services.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class SearchOptionsRepositoryImpl implements SearchOptionsRepository {
    private static final String SQL_NEW_ID = "select coalesce(max(seq), 0) + 1 from sqlite_sequence WHERE name = '" + TABLE_NAME + "'";
    private static final String SQL_NEW_ORDER_NUM = "select coalesce(max(" + FLD_ORDER_NUMBER + ") + 1, 1) from " + TABLE_NAME;
    private static final String SQL_GET_BY_ID = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_ID + " = ?";
    private static final String SQL_GET_ALL = "select " + ALL_FIELDS + " from " + TABLE_NAME + " order by " + FLD_ORDER_NUMBER;
    private static final String SQL_GET_BY_NAME = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NAME + " = ?";
    private static final String SQL_GET_NAMES_COUNT = "select count(1) as cnt from " + TABLE_NAME + " where " + FLD_NAME + " = ?";
    private static final String SQL_ADD = "insert into " + TABLE_NAME + "(" + ALL_FIELDS_ADD + ")" + " values (?,?,?,?)";
    private static final String SQL_UPDATE = "update " + TABLE_NAME + " set " + FLD_NAME + " = ?, " + FLD_OPTIONS + " = ?, " + FLD_ORDER_NUMBER + " = ?" + " where " + FLD_ID + " = ?";
    private static final String SQL_DELETE = "delete from " + TABLE_NAME + " where " + FLD_ID + " = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ConversionService conversionService;
    private final CommonService commonService;

    public SearchOptionsRepositoryImpl(JdbcTemplate jdbcTemplate, ConversionService conversionService, CommonService commonService) {
        this.jdbcTemplate = jdbcTemplate;
        this.conversionService = conversionService;
        this.commonService = commonService;
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
    public int nextOrderNumber() {
        Integer order_number = jdbcTemplate.queryForObject(SQL_NEW_ORDER_NUM, Integer.class);
        if (order_number == null) {
            throw new RuntimeException("Couldn't get new Order Number");
        }
        if (log.isDebugEnabled()) log.debug("got new order_number={}", order_number);
        return order_number;
    }

    @Override
    public List<SearchOptions> getAll() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL);
        List<SearchOptions> options = jdbcTemplate.query(SQL_GET_ALL, new OptionsRowMapper<SearchOptions>());
        if (log.isDebugEnabled()) log.debug("got search options={}", options);
        return options;
    }

    @Override
    public SearchOptions findById(long id) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, id={}", SQL_GET_BY_ID, id);
        SearchOptions option = jdbcTemplate.queryForObject(SQL_GET_BY_ID, new OptionsRowMapper<SearchOptions>(), id);
        if (log.isDebugEnabled()) log.debug("got option={}", option);
        return option;
    }

    @Override
    public SearchOptions findByName(String name) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, id={}", SQL_GET_BY_NAME, name);
        SearchOptions option = jdbcTemplate.queryForObject(SQL_GET_BY_NAME, new OptionsRowMapper<SearchOptions>(), name);
        if (log.isDebugEnabled()) log.debug("got option={}", option);
        return option;
    }

    @Override
    public boolean isNameAlreadyExists(String name) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, name={}", SQL_GET_NAMES_COUNT, name);
        Integer res = jdbcTemplate.queryForObject(SQL_GET_NAMES_COUNT, Integer.class, name);
        return (res != null && res > 0);
    }

    @Override
    public SearchOptions add(SearchOptions options) {
        long id = nextId();
        int orderNumber = nextOrderNumber();
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, options={}, orderNumber={}", SQL_ADD, options, orderNumber);
        jdbcTemplate.update(SQL_ADD,
                id,
                options.getName(),
                orderNumber,
                conversionService.convert(options.getParams(), String.class)
        );
        return findById(id);
    }

    @Override
    public SearchOptions update(SearchOptions options) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, options={}", SQL_UPDATE, options);
        jdbcTemplate.update(SQL_UPDATE,
                options.getName(),
                conversionService.convert(options.getParams(), String.class),
                options.getOrderNumber(),
                options.getId());
        return findById(options.getId());
    }

    @Override
    public void remove(SearchOptions options) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, options={}", SQL_DELETE, options);
        SearchOptions p = findById(options.getId());
        if (p != null) {
            jdbcTemplate.update(SQL_DELETE, options.getId());
            if (log.isDebugEnabled()) log.debug("Removed option={}", options);
        }
    }

    private final class OptionsRowMapper<T> implements RowMapper<SearchOptions> {
        @Override
        public SearchOptions mapRow(ResultSet rs, int rowNum) throws SQLException {
            String options = rs.getString(FLD_OPTIONS);
            SearchParams params = commonService.isNotEmpty(options) ? conversionService.convert(options, SearchParams.class) : null;
            return SearchOptions.builder()
                    .id(rs.getLong(FLD_ID))
                    .name(rs.getString(FLD_NAME))
                    .orderNumber(rs.getInt(FLD_ORDER_NUMBER))
                    .params(params)
                    .build();
        }
    }
}
