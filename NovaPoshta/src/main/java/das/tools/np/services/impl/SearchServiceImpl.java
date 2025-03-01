package das.tools.np.services.impl;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.SearchResult;
import das.tools.np.entity.search.LevenshteinDistance;
import das.tools.np.entity.search.SearchParams;
import das.tools.np.repository.ArchiveRepository;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.services.CargoStatusService;
import das.tools.np.services.CommonService;
import das.tools.np.services.SearchQueryProducer;
import das.tools.np.services.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    private static final int MAX_DISTANCE_OF_LENGTH_PERCENT = 20;
    private static final String SQL_GET_SIMPLE_RESULTS = "select " + SIMPLE_ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NUMBER + " like ?";
    private static final String SQL_GET_ARCHIVE_SIMPLE_RESULTS = "select " + SIMPLE_ALL_FIELDS + " from " + ArchiveRepository.TABLE_NAME + " where " + FLD_NUMBER + " like ?";
    private final JdbcTemplate jdbcTemplate;
    private final CargoNumberRepository numberRepository;
    private final ArchiveRepository archiveRepository;
    private final CommonService commonService;
    private final SearchQueryProducer queryProducer;

    public SearchServiceImpl(JdbcTemplate jdbcTemplate, CargoStatusService statusService, CargoNumberRepository numberRepository, ArchiveRepository archiveRepository, CommonService commonService, SearchQueryProducer queryProducer) {
        this.jdbcTemplate = jdbcTemplate;
        this.numberRepository = numberRepository;
        this.archiveRepository = archiveRepository;
        this.commonService = commonService;
        this.queryProducer = queryProducer;
    }

    @Override
    public List<CargoNumber> search(String simpleValue) {
        List<SearchResult> results = getSimpleSearchResults(simpleValue);
        List<CargoNumber> numbers = new ArrayList<>();
        results.forEach(s -> numbers.add(numberRepository.findById(s.getId())));
        if (log.isDebugEnabled()) log.debug("got search numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> searchArchive(String simpleValue) {
        List<SearchResult> results = getSimpleSearchArchiveResults(simpleValue);
        List<CargoNumber> numbers = new ArrayList<>();
        results.forEach(s -> numbers.add(archiveRepository.findByNumber(s.getNumber())));
        if (log.isDebugEnabled()) log.debug("got search archive numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> search(SearchParams params) {
        List<SearchResult> results = getExtSearchResults(params);
        List<CargoNumber> numbers = new ArrayList<>();
        results.forEach(s -> numbers.add(numberRepository.findById(s.getId())));
        if (log.isDebugEnabled()) log.debug("got search numbers={}", numbers);
        return numbers;
    }

    @Override
    public List<CargoNumber> searchArchive(SearchParams params) {
        List<SearchResult> results = getExtSearchArchiveResults(params);
        List<CargoNumber> numbers = new ArrayList<>();
        results.forEach(s -> numbers.add(archiveRepository.findByNumber(s.getNumber())));
        if (log.isDebugEnabled()) log.debug("got search archive numbers={}", numbers);
        return numbers;
    }

    private List<SearchResult> getSimpleSearchResults(String simpleValue) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, value={}", SQL_GET_SIMPLE_RESULTS, simpleValue);
        List<SearchResult> results = jdbcTemplate.query(SQL_GET_SIMPLE_RESULTS, new SearchResultRowMapper<SearchResult>(), ('%' + simpleValue + '%'));
        if (log.isDebugEnabled()) log.debug("by SQL got simple search results={}", results);
        return results;
    }

    private List<SearchResult> getSimpleSearchArchiveResults(String simpleValue) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, value={}", SQL_GET_ARCHIVE_SIMPLE_RESULTS, simpleValue);
        List<SearchResult> results = jdbcTemplate.query(SQL_GET_ARCHIVE_SIMPLE_RESULTS, new SearchResultRowMapper<SearchResult>(), ('%' + simpleValue + '%'));
        if (log.isDebugEnabled()) log.debug("by SQL got simple search archive results={}", results);
        return results;
    }

    private List<SearchResult> getExtSearchResults(SearchParams params) {
        String sql = queryProducer.getQuery(params);
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", sql);
        return getSearchResults(params, sql);
    }

    private List<SearchResult> getExtSearchArchiveResults(SearchParams params) {
        String sql = queryProducer.getArchiveQuery(params);
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", sql);
        return getSearchResults(params, sql);
    }

    private List<SearchResult> getSearchResults(SearchParams params, String sql) {
        List<SearchResult> searchResults = jdbcTemplate.query(sql, new SearchResultRowMapper<SearchResult>());
        if (log.isDebugEnabled()) log.debug("by SQL got searchResults={}", searchResults);
        List<SearchResult> results = new ArrayList<>(searchResults.size());
        for (SearchResult r : searchResults) {
            if (commonService.isNotEmpty(r.getAll())) {
                if (existsInWords(r.getAll(), params.getSearchText())) {
                    results.add(r);
                }
            }
        }
        if (log.isDebugEnabled()) log.debug("got extended search results={}", results);
        return results;
    }

    private boolean existsInWords(String s, String searchText) {
        String lowerCaseString = s.toLowerCase();
        String lowerCaseSearch = searchText.toLowerCase();
        if (lowerCaseString.contains(lowerCaseSearch)) return true;
        if (distanceOfLengthPercent(lowerCaseString, lowerCaseSearch) <= MAX_DISTANCE_OF_LENGTH_PERCENT) return true;
        String[] words = lowerCaseString
                .replaceAll("[!?,.-;:]", " ")
                .replaceAll("[(){}\\[\\]]", "")
                .split(" ");
        if (log.isDebugEnabled()) log.debug("got words={}", Arrays.toString(words));
        for (String w : words) {
            if (w.contains(lowerCaseSearch) ||
                    (distanceOfLengthPercent(w, lowerCaseSearch) <= MAX_DISTANCE_OF_LENGTH_PERCENT)) {
                return true;
            }
        }
        return false;
    }

    private int distanceOfLengthPercent(String currentWord, String searchString) {
        return LevenshteinDistance.calculateDynamic(searchString.toLowerCase(), currentWord.toLowerCase()) * 100 / searchString.length();
    }

    private static final class SearchResultRowMapper<T> implements RowMapper<SearchResult> {
        @Override
        public SearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            return SearchResult.builder()
                    .id(rs.getLong(FLD_ID))
                    .number(rs.getString(FLD_NUMBER))
                    .descr(rs.getString(FLD_DESCR))
                    .cityFrom(rs.getString(FLD_CITY_FROM))
                    .all(rs.getString(FLD_ALL_IN_ONE))
                    .build();
        }
    }
}
