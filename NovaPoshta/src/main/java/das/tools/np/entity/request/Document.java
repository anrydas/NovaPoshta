package das.tools.np.entity.request;

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
public class Document {
    @JsonProperty("DocumentNumber")
    private String documentNumber;
    @JsonProperty("Phone")
    private String phoneNumber;
}
