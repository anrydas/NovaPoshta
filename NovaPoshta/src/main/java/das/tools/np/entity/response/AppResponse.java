package das.tools.np.entity.response;

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
public class AppResponse {
    @JsonProperty("success")
    private boolean isSuccess;
    private ResponseData[] data;
    private String[] errors;
    private String[] warnings;
    private String[] info;
    private String[] messageCodes;
    private String[] errorCodes;
    private String[] warningCodes;
    private String[] infoCodes;
}
