package das.tools.np.config;

import jakarta.annotation.PostConstruct;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
public class AppConfig {
    private final Environment env;

    @PostConstruct
    public void setAwtHeadlessOff () {
        /* to prevent Headless exception. It has thrown when code that is dependent on a keyboard, display, or mouse
           is called in an environment that does not support a keyboard, display, or mouse. */
        System.setProperty("java.awt.headless", "false");
    }

    public AppConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public RestTemplate restTemplate() {
       return new RestTemplate();
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("driverClassName")));
        dataSource.setUrl(env.getProperty("url"));
        dataSource.setUsername(env.getProperty("user"));
        dataSource.setPassword(env.getProperty("password"));
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public GlyphFont glyphFont() {
        return GlyphFontRegistry.font("FontAwesome");
    }
}
