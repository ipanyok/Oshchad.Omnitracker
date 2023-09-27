package ua.datastech.omnitracker.model.oim;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class OmniTrackerRequest {

    @EqualsAndHashCode.Include
    private String objectID;
    private String subject;
    private String description;
    private String customer;
    private String ContactInfo;

    @EqualsAndHashCode.Include
    private String ServiceID;

    @EqualsAndHashCode.Include
    private String ServiceTypeID;
    private String ServiceCategory;
    private String InfluenceCode;
    private String Deadline;
    private String SpecificationText;
    private AdditionalInfo additionalInfo;
}
