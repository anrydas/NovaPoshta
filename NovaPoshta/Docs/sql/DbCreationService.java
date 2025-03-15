package das.tools.np.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@Slf4j
public class DbCreationService {
    public final String SQL_GET_TABLES_COUNT = "select count(1) from sqlite_master where type = 'table' " +
            "and name in('archive','properties','groups','numbers','extra_phones','number_to_phone','search_history', 'search_params')";
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public DbCreationService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void checkDbExistence() {
        log.info("checkDbExistence started");
        if (getTablesCount() < 8) {
            if (log.isDebugEnabled()) log.debug("--- Creating new DB ---");
            Resource resource = new ClassPathResource("sql/schema.sql");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
            populator.execute(dataSource);
            if (log.isDebugEnabled()) log.debug("--- DB structure created ---");
            resource = new ClassPathResource("sql/data.sql");
            populator = new ResourceDatabasePopulator(resource);
            populator.execute(dataSource);
            if (log.isDebugEnabled()) log.debug("--- Initial data inserted ---");
        }
    }

    private int getTablesCount() {
        Integer cnt = jdbcTemplate.queryForObject(SQL_GET_TABLES_COUNT, Integer.class);
        if (cnt == null) {
            throw new RuntimeException("Couldn't execute SQL");
        }
        return cnt;
    }
}
