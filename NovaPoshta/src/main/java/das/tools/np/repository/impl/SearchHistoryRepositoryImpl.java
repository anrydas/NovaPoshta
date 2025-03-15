package das.tools.np.repository.impl;

import das.tools.np.entity.db.SearchHistory;
import das.tools.np.repository.SearchHistoryRepository;
import das.tools.np.services.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
@Slf4j
public class SearchHistoryRepositoryImpl implements SearchHistoryRepository {
    private static final String SQL_NEW_ID = "select coalesce(max(seq), 0) + 1 from sqlite_sequence WHERE name = '" + TABLE_NAME + "'";
    private static final String SQL_GET_BY_ID = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_ID + " = ?";
    private static final String SQL_GET_BY_TEXT = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_SEARCH_TEXT + " = ?";
    private static final String SQL_GET_ALL = "select " + ALL_FIELDS + " from " + TABLE_NAME;
    private static final String SQL_GET_REC_COUNT = "select count(1) as cnt from " + TABLE_NAME + " where " + FLD_SEARCH_TEXT + " = ?";
    private static final String SQL_ADD = "insert into " + TABLE_NAME + "(" + ALL_FIELDS_ADD + ")" + " values (?,?)";
    private static final String SQL_DELETE_OLD = "delete from " + TABLE_NAME +
            " where " + FLD_ID + " in(select id from (select "+FLD_ID+", row_number() over (order by "+FLD_CREATED+" desc) rn) where rn > ?)";

    private final JdbcTemplate jdbcTemplate;
    private final ConfigService configService;

    public SearchHistoryRepositoryImpl(JdbcTemplate jdbcTemplate, ConfigService configService) {
        this.jdbcTemplate = jdbcTemplate;
        this.configService = configService;
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
    public SearchHistory findById(long id) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, id={}", SQL_GET_BY_ID, id);
        SearchHistory history = jdbcTemplate.queryForObject(SQL_GET_BY_ID, new HistoryRowMapper<SearchHistory>(), id);
        if (log.isDebugEnabled()) log.debug("got history={}", history);
        return history;
    }

    @Override
    public SearchHistory findByText(String searchText) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, text={}", SQL_GET_BY_TEXT, searchText);
        SearchHistory history = jdbcTemplate.queryForObject(SQL_GET_BY_TEXT, new HistoryRowMapper<SearchHistory>(), searchText);
        if (log.isDebugEnabled()) log.debug("got history={}", history);
        return history;
    }

    @Override
    public List<SearchHistory> getAll() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL);
        List<SearchHistory> historyList = jdbcTemplate.query(SQL_GET_ALL, new HistoryRowMapper<SearchHistory>());
        if (log.isDebugEnabled()) log.debug("got search historyList={}", historyList);
        return historyList;
    }

    @Override
    public String[] getAllAsStrings() {
        List<SearchHistory> historyList = getAll();
        String[] res = new String[historyList.size()];
        for (int i = 0; i < historyList.size(); i++) {
            res[i] = historyList.get(i).getSearchText();
        }
        if (log.isDebugEnabled()) log.debug("got search history strings={}", Arrays.toString(res));
        return res;
    }

    @Override
    public boolean isRecordExists(String value) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, value={}", SQL_GET_REC_COUNT, value);
        Integer res = jdbcTemplate.queryForObject(SQL_GET_REC_COUNT, Integer.class, value);
        return (res != null && res > 0);
    }

    @Override
    public SearchHistory add(String searchText) {
        if (configService.getMaxSearchHistoryRecords() <= 0) {
            return null;
        }
        removeOlds();
        if (!isRecordExists(searchText)) {
            long id = nextId();
            if (log.isDebugEnabled()) log.debug("Executing SQL={}, searchText={}", SQL_ADD, searchText);
            jdbcTemplate.update(SQL_ADD, id, searchText);
            return findById(id);
        }
        return findByText(searchText);
    }

    @Override
    public void removeOlds() {
        int maxRecords = configService.getMaxSearchHistoryRecords();
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, MAX_RECORDS={}", SQL_DELETE_OLD, maxRecords);
        int recs = jdbcTemplate.update(SQL_DELETE_OLD, maxRecords);
        if (log.isDebugEnabled()) log.debug("removed={} history records", recs);
    }

    private static final class HistoryRowMapper<T> implements RowMapper<SearchHistory> {
        @Override
        public SearchHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            return SearchHistory.builder()
                    .id(rs.getLong(FLD_ID))
                    .searchText(rs.getString(FLD_SEARCH_TEXT))
                    .build();
        }
    }
}
