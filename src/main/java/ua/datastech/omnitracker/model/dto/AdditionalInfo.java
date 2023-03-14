package ua.datastech.omnitracker.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AdditionalInfo {

    @EqualsAndHashCode.Include
    private String empNumber;
    private String mainBranch;
    private String tmpBranch;
    private String startDate;
    private String endDate;
***REMOVED***
