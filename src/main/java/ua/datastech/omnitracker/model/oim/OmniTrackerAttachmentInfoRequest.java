package ua.datastech.omnitracker.model.oim;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class OmniTrackerAttachmentInfoRequest {

    @EqualsAndHashCode.Include
    private String objectID;
    private List<Attachments> attachments;
    private AdditionalInfo additionalInfo;

}
