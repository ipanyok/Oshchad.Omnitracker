package ua.datastech.omnitracker.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OimUserDto {

    private Long usrKey;
    private Long id;
    private String objectId;
    private String sourceId;
    private String empNumber;
    private String mainBranch;
    private String tmpBranch;
    private String currentBranch;
    private String startDate;
    private String endDate;
    private Boolean isPickupSent;
    private Boolean isClosureSent;
    private String adLogin;
    private String action;
    private String actionDate;
    private Long oid;
    private String fileName;
    private Long fileSize;
    private String attachment;

***REMOVED***
