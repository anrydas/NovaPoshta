package das.tools.np.config;

import das.tools.np.services.impl.UTF8ControlImpl;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ResourceBundle;

@Configuration
@Slf4j
public class GuiConfig {
    @Bean(name = "utf8Control")
    public ResourceBundle.Control getUtf8Control() {
        return new UTF8ControlImpl();
    }
    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        return new SpringFxWeaver(applicationContext);
    }
}
