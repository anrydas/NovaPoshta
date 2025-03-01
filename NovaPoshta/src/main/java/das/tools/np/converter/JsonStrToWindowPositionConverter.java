package das.tools.np.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import das.tools.np.entity.WindowPosition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class JsonStrToWindowPositionConverter  implements Converter<String, WindowPosition> {
    @Override
    public WindowPosition convert(String source) {
        WindowPosition windowPosition = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            windowPosition = objectMapper.readValue(source, WindowPosition.class);
            return windowPosition;
        } catch (JsonProcessingException e) {
            log.error("Couldn't convert Json string to SearchParams: ", e);
            return WindowPosition.builder().build();
        }
    }
}
