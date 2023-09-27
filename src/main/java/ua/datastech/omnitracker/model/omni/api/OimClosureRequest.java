package ua.datastech.omnitracker.model.omni.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class OimClosureRequest {

    private String externalID;
    private String objectID;
    private ResponseCodeEnum closureCode;
    private String solution;
    private String solutionSpecification;

}
