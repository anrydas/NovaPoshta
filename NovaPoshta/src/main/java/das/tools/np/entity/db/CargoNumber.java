package das.tools.np.entity.db;

import das.tools.np.entity.response.ResponseData;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CargoNumber {
    @Builder.Default
    private long id = -1;
    private String number;
    private Group group;
    private String groupName;
    private String descr;
    private CargoStatus appStatus;
    @Builder.Default
    private NumberType numberType = NumberType.UNDEF;
    private String comment;
    private String dateCreated;
    private float weight;
    private float cost;
    private float seatsAmount;
    private String description;
    private String cargoType;
    private int statusCode;
    private String announcedPrice;
    private String scheduledDeliveryDate;
    private String recipientFullName;
    private String cityRecipient;
    private String warehouseRecipient;
    private String warehouseRecipientNumber;
    private String phoneRecipient;
    private String recipientAddress;
    private String citySender;
    private String phoneSender;
    private String warehouseSender;
    private String senderAddress;
    private boolean autoUpdated;
    private ResponseData fullData;
    private Date created;
    private Date updated;
}
