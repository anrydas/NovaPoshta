package das.tools.np.config;

import das.tools.np.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ConversionConfig {
    @Bean
    public ConversionService getConversionService() {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters(getConverters(bean));
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    private Set<Converter> getConverters(ConversionServiceFactoryBean bean) {
        Set<Converter> converters = new HashSet<>();
        converters.add(new GZipToResponseDataConverter());
        converters.add(new ResponseDataToGzipConverter());
        converters.add(new StringArrayIdsToDocumentsArrayConverter());
        converters.add(new ResponseDataToCargoNumberConverter());
        CargoNumberToSimpleConverter cargoNumberToSimple = new CargoNumberToSimpleConverter();
        converters.add(cargoNumberToSimple);
        converters.add(new CargoNumbersToSimpleNumbersConverter(cargoNumberToSimple));
        converters.add(new SearchParamsToJsonStrConverter());
        converters.add(new JsonStrToSearchParamsConverter());
        converters.add(new WindowPositionToJsonStrConverter());
        converters.add(new JsonStrToWindowPositionConverter());
        return converters;
    }
}
