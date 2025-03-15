package das.tools.np.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import das.tools.np.entity.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

@Slf4j
public class GZipToResponseDataConverter implements Converter<String, ResponseData> {
    @Override
    public ResponseData convert(String source) {
        ResponseData responseData = null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            responseData = objectMapper.readValue(decompress(source), ResponseData.class);
            return responseData;
        } catch (JsonProcessingException e) {
            log.error("Couldn't convert GZip to ResponseData: ", e);
            return ResponseData.builder().build();
        }
    }

    private static String decompress(String source) {
        try(GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(source)));
            BufferedReader bf = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line=bf.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
