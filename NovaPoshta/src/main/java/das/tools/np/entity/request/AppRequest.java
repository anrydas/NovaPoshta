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
public class AppRequest {
    private String apiKey;
    @Builder.Default
    private String modelName = "TrackingDocument";
    @Builder.Default
    private String calledMethod = "getStatusDocuments";
    @JsonProperty("methodProperties")
    private MethodProperties methodProperties;
}
