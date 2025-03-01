package das.tools.np.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import das.tools.np.entity.search.SearchParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class JsonStrToSearchParamsConverter implements Converter<String, SearchParams> {
    @Override
    public SearchParams convert(String source) {
        SearchParams searchParams = null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            searchParams = objectMapper.readValue(source, SearchParams.class);
            return searchParams;
        } catch (JsonProcessingException e) {
            log.error("Couldn't convert Json string to SearchParams: ", e);
            return SearchParams.builder().build();
        }
    }
}
