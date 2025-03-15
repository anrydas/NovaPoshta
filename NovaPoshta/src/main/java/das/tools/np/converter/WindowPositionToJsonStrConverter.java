package das.tools.np.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import das.tools.np.entity.WindowPosition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class WindowPositionToJsonStrConverter implements Converter<WindowPosition,String> {

    @Override
    public String convert(WindowPosition source) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            log.error("Couldn't convert WindowPosition to Json: ", e);
            return "";
        }
    }
}
