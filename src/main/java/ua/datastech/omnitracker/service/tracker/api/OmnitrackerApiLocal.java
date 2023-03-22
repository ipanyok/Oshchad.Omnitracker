package ua.datastech.omnitracker.service.tracker.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class OmnitrackerApiLocal implements OmnitrackerApiService {


    private final JdbcQueryService jdbcQueryService;

    @Override
    public void callOmniTrackerClosureService(String empNumber, String adLogin, String objectId, ResponseCodeEnum closureCode, String solution, String solutionSpecification) {
        if (empNumber != null) {
            jdbcQueryService.updateOmniRequestQuery(empNumber, objectId, Collections.singletonMap("IS_CLOSURE_SENT", "1"));
        ***REMOVED***
        if (adLogin != null) {
            jdbcQueryService.updateOmniBlockRequestQuery(adLogin, objectId, Collections.singletonMap("IS_CLOSURE_SENT", "1"));
        ***REMOVED***
        log.info("callOmniTrackerClosureService()...");
    ***REMOVED***

    @Override
    public void callOmniTrackerPickupService(String empNumber, String adLogin, String objectId) {
        if (empNumber != null) {
            jdbcQueryService.updateOmniRequestQuery(empNumber, objectId, Collections.singletonMap("IS_PICKUP_SENT", "1"));
        ***REMOVED***
        if (adLogin != null) {
            jdbcQueryService.updateOmniBlockRequestQuery(adLogin, objectId, Collections.singletonMap("IS_PICKUP_SENT", "1"));
        ***REMOVED***
        log.info("callOmniTrackerPickupService()...");
    ***REMOVED***
***REMOVED***
