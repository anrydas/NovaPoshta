package das.tools.np.converter;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.SimpleNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CargoNumberToSimpleConverter implements Converter<CargoNumber, SimpleNumber> {
    private final SimpleDateFormat CREATED_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Override
    public SimpleNumber convert(CargoNumber source) {
        return SimpleNumber.builder()
                .id(source.getId())
                .number(source.getNumber())
                .appStatus(source.getAppStatus())
                .numberType(source.getNumberType())
                .description(source.getDescription())
                .comment(source.getComment())
                .status(source.getStatusCode())
                .createDate(getCreatedDate(source.getDateCreated()))
                .created((source.getCreated()))
                .updated((source.getUpdated()))
                .build();
    }

    private Date getCreatedDate(String date) {
        try {
            return CREATED_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            log.error("Error parsing date {}", date);
            return new Date(0);
        }
    }
}
