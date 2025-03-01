package das.tools.np.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import das.tools.np.entity.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class ResponseDataToGzipConverter implements Converter<ResponseData, String> {

    @Override
    public String convert(ResponseData source) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return compress(objectMapper.writeValueAsString(source));
        } catch (JsonProcessingException e) {
            log.error("Couldn't convert ResponseData to GZip: ", e);
            return "";
        }
    }

    private static String compress(String source) {
        try(ByteArrayOutputStream arr = new ByteArrayOutputStream();
            GZIPOutputStream os = new GZIPOutputStream(arr)) {
            os.write(source.getBytes(StandardCharsets.UTF_8));
            os.finish();
            return Base64.getEncoder().encodeToString(arr.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
