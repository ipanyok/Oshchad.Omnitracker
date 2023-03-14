package ua.datastech.omnitracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.datastech.omnitracker.model.oim.OmniTrackerRequest;
import ua.datastech.omnitracker.model.omni.api.OmniTrackerResponse;
import ua.datastech.omnitracker.service.tracker.OmnitrackerService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerController {

    private final OmnitrackerService omnitrackerService;

    @PostMapping(value = "/api/call-dispatch-req", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OmniTrackerResponse omnitrackerApi(@RequestBody OmniTrackerRequest request) {
        log.info("call-dispatch-req()... objectId: " + request.getObjectID());
        omnitrackerService.saveOmniRequest(request);
        return OmniTrackerResponse.builder()
                .externalID(request.getObjectID())
                .build();
    ***REMOVED***

***REMOVED***
