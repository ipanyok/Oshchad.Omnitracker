package ua.datastech.omnitracker.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class OmniTrackerRequest {
    private String objectID;
    private String subject;
    private String description;
    private String customer;
    private String ContactInfo;
    private String ServiceID;
    private String ServiceTypeID;
    private String ServiceCategory;
    private String InfluenceCode;
    private String Deadline;
    private String SpecificationText;
    private AdditionalInfo additionalInfo;
***REMOVED***
