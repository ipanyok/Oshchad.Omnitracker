package ua.datastech.omnitracker.service.tracker.api;

import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;

public interface OmnitrackerApiService {

    void callOmniTrackerClosureService(String empNumber, String adLogin, String objectId, ResponseCodeEnum closureCode, String solution, String solutionSpecification);
    void callOmniTrackerPickupService(String empNumber, String adLogin, String objectId);

***REMOVED***
