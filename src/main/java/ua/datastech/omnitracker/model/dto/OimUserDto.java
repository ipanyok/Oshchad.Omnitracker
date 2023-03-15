package ua.datastech.omnitracker.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OimUserDto {

    private Long usrKey;
    private String objectId;
    private String empNumber;
    private String mainBranch;
    private String tmpBranch;
    private String startDate;
    private String endDate;
    private Boolean isPickupSent;
    private Boolean isClosureSent;

***REMOVED***
