package ua.datastech.omnitracker.service.tracker.api;

import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;

public interface OmnitrackerApiService {

    void callOmniTrackerClosureService(String externalID, String objectID, ResponseCodeEnum closureCode, String solution, String solutionSpecification);
    void callOmniTrackerPickupService(OimUserDto oimUserDto);

***REMOVED***
