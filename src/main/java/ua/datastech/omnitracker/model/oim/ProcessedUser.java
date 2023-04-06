package ua.datastech.omnitracker.model.oim;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProcessedUser {

    private String adLogin;
    private String processOimUserScript;
    private String provisioningScript;

***REMOVED***
