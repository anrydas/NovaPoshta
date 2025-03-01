package das.tools.np.entity.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import das.tools.np.entity.db.CargoStatus;
import das.tools.np.entity.db.NumberType;
import das.tools.np.entity.db.Status;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SearchParams {
    @JsonProperty("text")
    private String searchText = "";
    private List<String> fields = new ArrayList<>();
    private SearchStringValue group = new SearchStringValue();
    private SearchStringValue groupText = new SearchStringValue();
    @JsonProperty("type")
    private SearchTypeValue<NumberType> numberType;
    @JsonProperty("app_status")
    private SearchTypeValue<CargoStatus> appStatus;
    @JsonProperty("status")
    private SearchTypeValue<Status> status;
    private SearchDateValue create;
    private SearchDateValue schedule;
    private SearchIntValue weight;
    private SearchIntValue cost;
    private SearchIntValue price;
    private SearchIntValue seats;
}
