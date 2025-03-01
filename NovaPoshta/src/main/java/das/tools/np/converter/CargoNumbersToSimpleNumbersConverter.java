package das.tools.np.converter;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.SimpleNumber;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class CargoNumbersToSimpleNumbersConverter implements Converter<List<CargoNumber>,List<SimpleNumber>> {
    private final CargoNumberToSimpleConverter converter;

    public CargoNumbersToSimpleNumbersConverter(CargoNumberToSimpleConverter converter) {
        this.converter = converter;
    }

    @Override
    public List<SimpleNumber> convert(List<CargoNumber> source) {
        List<SimpleNumber> result = new ArrayList<>(source.size());
        for (CargoNumber n : source) {
            result.add(converter.convert(n));
        }
        return result;
    }
}
