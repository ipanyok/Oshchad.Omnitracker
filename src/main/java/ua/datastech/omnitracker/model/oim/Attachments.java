package ua.datastech.omnitracker.model.oim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attachments {

    @EqualsAndHashCode.Include
    @JsonProperty("OID")
    private Long oid;
    private String fileName;
    private Long fileSize;

}
