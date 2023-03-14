package ua.datastech.omnitracker.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class OimPickupRequest {

    private String externalID;
    private String objectID;
    private LocalDateTime externalDeadline;
    private String responsible;
    private String responsibleInfo;

***REMOVED***
