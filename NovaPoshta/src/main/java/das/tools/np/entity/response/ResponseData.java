package das.tools.np.entity.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {
    @JsonProperty("Number")
    private String number;
    @JsonProperty("DateCreated")
    private String dateCreated;
    @JsonProperty("DocumentWeight")
    private float weight;
    @JsonProperty("DocumentCost")
    private float cost;
    @JsonProperty("PayerType")
    private String payerType;
    @JsonProperty("CargoDescriptionString")
    private String description;
    @JsonProperty("CargoType")
    private String cargoType;
    @JsonProperty("ScheduledDeliveryDate")
    private String scheduledDeliveryDate; // calculated delivery date
    @JsonProperty("Status")
    private String status;
    @JsonProperty("StatusCode")
    private String statusCode;
    @JsonProperty("ServiceType")
    private String serviceType;
    @JsonProperty("InternationalDeliveryType")
    private String internationalDeliveryType;
    @JsonProperty("SeatsAmount")
    private int seatsAmount;
    @JsonProperty("TrackingUpdateDate")
    private String trackingUpdateDate;
    @JsonProperty("DateFirstDayStorage")
    private String dateFirstDayStorage;
    @JsonProperty("AnnouncedPrice")
    private String announcedPrice;
    @JsonProperty("ActualDeliveryDate")
    private String actualDeliveryDate;
    @JsonProperty("DeliveryTimeframe")
    private String deliveryTimeframe;
    @JsonProperty("StorageAmount")
    private String storageAmount;
    @JsonProperty("StoragePrice")
    private float storagePrice;
    @JsonProperty("FreeShipping")
    private String freeShipping;
    @JsonProperty("LoyaltyCardRecipient")
    private String loyaltyCardRecipient;
    @JsonProperty("AviaDelivery")
    private String aviaDelivery;
    @JsonProperty("DaysStorageCargo")
    private int daysStorageCargo;
    // RECIPIENT
    @JsonProperty("RecipientFullName")
    private String recipientFullName;
    @JsonProperty("CityRecipient")
    private String cityRecipient;
    @JsonProperty("WarehouseRecipient")
    private String warehouseRecipient;
    @JsonProperty("WarehouseRecipientNumber")
    private String warehouseRecipientNumber;
    @JsonProperty("PhoneRecipient")
    private String phoneRecipient;
    @JsonProperty("RecipientFullNameEW")
    private String recipientFullNameEW;
    @JsonProperty("RecipientAddress")
    private String recipientAddress;
    @JsonProperty("RecipientDateTime")
    private String recipientDateTime; // when parcel was taken
    // SENDER
    @JsonProperty("CitySender")
    private String citySender;
    @JsonProperty("PhoneSender")
    private String phoneSender;
    @JsonProperty("WarehouseSender")
    private String warehouseSender;
    @JsonProperty("SenderAddress")
    private String senderAddress;
    @JsonProperty("SenderFullNameEW")
    private String senderFullNameEW;
    // WEIGHT
    @JsonProperty("FactualWeight")
    private float factWeight;
    @JsonProperty("VolumeWeight")
    private float volumeWeight;
    @JsonProperty("CheckWeight")
    private float checkWeight;
    @JsonProperty("CalculatedWeight")
    private float calculatedWeight;
    @JsonProperty("CheckWeightMethod")
    private String checkWeightMethod;
    // PAYMENT
    @JsonProperty("PaymentMethod")
    private String paymentMethod;
    @JsonProperty("PaymentStatus")
    private String paymentStatus;
    @JsonProperty("PaymentStatusDate")
    private String paymentStatusDate;
    @JsonProperty("AmountToPay")
    private String amountToPay;
    @JsonProperty("AmountPaid")
    private String amountPaid;
    // Additional
    @JsonProperty("UndeliveryReasonsSubtypeDescription")
    private String undeliveryReasonsSubtypeDescription;
    @JsonProperty("LastCreatedOnTheBasisNumber")
    private String lastCreatedOnTheBasisNumber;
    @JsonProperty("WarehouseRecipientInternetAddressRef")
    private String warehouseRecipientInternetAddressRef;
    @JsonProperty("MarketplacePartnerToken")
    private String marketplacePartnerToken;
    @JsonProperty("ClientBarcode")
    private String clientBarcode;
    @JsonProperty("CounterpartyRecipientDescription")
    private String counterpartyRecipientDescription;
    @JsonProperty("DateScan")
    private String dateScan;
    //@JsonProperty("UndeliveryReasons")
    //private String undeliveryReasons;
    @JsonProperty("DatePayedKeeping")
    private String datePayedKeeping;
    @JsonProperty("CardMaskedNumber")
    private String cardMaskedNumber;
    @JsonProperty("ExpressWaybillPaymentStatus")
    private String expressWaybillPaymentStatus;
    @JsonProperty("ExpressWaybillAmountToPay")
    private float expressWaybillAmountToPay;
    @JsonProperty("DateReturnCargo")
    private String dateReturnCargo;
    @JsonProperty("DateMoving")
    private String dateMoving;
    @JsonProperty("AdditionalInformationEW")
    private String additionalInformationEW;
    @JsonProperty("PostomatV3CellReservationNumber")
    private String postomatV3CellReservationNumber;
    @JsonProperty("OwnerDocumentNumber")
    private String ownerDocumentNumber;
    @JsonProperty("LastAmountReceivedCommissionGM")
    private float lastAmountReceivedCommissionGM;
    //@JsonProperty("CreatedOnTheBasis")
    //private String createdOnTheBasis;
    @JsonProperty("UndeliveryReasonsDate")
    private String undeliveryReasonsDate;
    @JsonProperty("CategoryOfWarehouse")
    private String categoryOfWarehouse;
    @JsonProperty("WarehouseRecipientAddress")
    private String warehouseRecipientAddress;
    @JsonProperty("WarehouseSenderAddress")
    private String warehouseSenderAddress;
    @JsonProperty("CounterpartySenderType")
    private String counterpartySenderType;
    @JsonProperty("CargoReturnRefusal")
    private String cargoReturnRefusal;
    //@JsonProperty("Packaging")
    //private String packaging;
    //@JsonProperty("PartialReturnGoods")
    //private String partialReturnGoods;
    @JsonProperty("SecurePayment")
    private boolean securePayment;
    @JsonProperty("PossibilityChangeCash2Card")
    private boolean possibilityChangeCash2Card;
    @JsonProperty("PossibilityChangeDeliveryIntervals")
    private boolean possibilityChangeDeliveryIntervals;
    @JsonProperty("PossibilityTermExtensio")
    private boolean possibilityTermExtensio;
}
