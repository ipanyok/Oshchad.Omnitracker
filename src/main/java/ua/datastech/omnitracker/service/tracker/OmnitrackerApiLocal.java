package ua.datastech.omnitracker.service.tracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.dto.ResponseCodeEnum;

@Service
@Slf4j
@Profile("local")
public class OmnitrackerApiLocal implements OmnitrackerApiService {


    @Override
    public void callOmniTrackerClosureService(String externalID, String objectID, ResponseCodeEnum closureCode, String solution, String solutionSpecification) {
        log.info("callOmniTrackerClosureService()...");
    ***REMOVED***

    @Override
    public void callOmniTrackerPickupService(OimUserDto oimUserDto) {
        log.info("callOmniTrackerPickupService()...");
    ***REMOVED***
***REMOVED***
