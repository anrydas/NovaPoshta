package das.tools.np.converter;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.response.ResponseData;
import org.springframework.core.convert.converter.Converter;

public class ResponseDataToCargoNumberConverter implements Converter<ResponseData, CargoNumber> {
    @Override
    public CargoNumber convert(ResponseData source) {
        return CargoNumber.builder()
                .number(source.getNumber())
                .dateCreated(source.getDateCreated())
                .weight(source.getWeight())
                .cost(source.getCost())
                .seatsAmount(source.getSeatsAmount())
                .description(source.getDescription())
                .cargoType(source.getCargoType())
                .statusCode(Integer.parseInt(source.getStatusCode()))
                .announcedPrice(source.getAnnouncedPrice())
                .scheduledDeliveryDate(source.getScheduledDeliveryDate())
                .recipientFullName(source.getRecipientFullName())
                .cityRecipient(source.getCityRecipient())
                .warehouseRecipient(source.getWarehouseRecipient())
                .warehouseRecipientNumber(source.getWarehouseRecipientNumber())
                .phoneRecipient(source.getPhoneRecipient())
                .recipientAddress(source.getRecipientAddress())
                .citySender(source.getCitySender())
                .phoneSender(source.getPhoneSender())
                .warehouseSender(source.getWarehouseSender())
                .senderAddress(source.getSenderAddress())
                .build();
    }
}
