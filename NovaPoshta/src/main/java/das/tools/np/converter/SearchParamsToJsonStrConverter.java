package das.tools.np.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import das.tools.np.entity.search.SearchParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class SearchParamsToJsonStrConverter implements Converter<SearchParams,String> {
    @Override
    public String convert(SearchParams source) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            log.error("Couldn't convert SearchParams to Json: ", e);
            return "";
        }
    }
}
