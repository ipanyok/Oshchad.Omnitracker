package ua.datastech.omnitracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.datastech.omnitracker.model.oim.OmniTrackerAttachmentInfoRequest;
import ua.datastech.omnitracker.model.oim.OmniTrackerRequest;
import ua.datastech.omnitracker.model.omni.api.OmniTrackerResponse;
import ua.datastech.omnitracker.service.script.PowerShellExecutor;
import ua.datastech.omnitracker.service.tracker.OmnitrackerService;

import java.util.Arrays;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerController {

    private final OmnitrackerService omnitrackerService;
    private final PowerShellExecutor powerShellExecutor;

    @PostMapping(value = "/api/call-dispatch-req", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OmniTrackerResponse omnitrackerApi(@RequestBody OmniTrackerRequest request) {
        log.info("call-dispatch-req()... objectId: " + request.getObjectID());
        omnitrackerService.saveOmniRequest(request);
        return OmniTrackerResponse.builder()
                .externalID(request.getObjectID())
                .build();
    ***REMOVED***

    @PostMapping(value = "/block/call-dispatch-req", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OmniTrackerResponse omnitrackerApiBlocking(@RequestBody OmniTrackerRequest request) {
        log.info("call-dispatch-req()... objectId: " + request.getObjectID());
        omnitrackerService.saveOmniBlockRequest(request);
        return OmniTrackerResponse.builder()
                .externalID(request.getObjectID())
                .build();
    ***REMOVED***

    @PostMapping(value = "/block/call-user-info", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OmniTrackerResponse omnitrackerApiGetAttachmentsInfo(@RequestBody OmniTrackerAttachmentInfoRequest request) {
        log.info("call-update-req()... objectId: " + request.getObjectID());
        omnitrackerService.saveOmniAttachmetRequest(request);
        return OmniTrackerResponse.builder()
                .externalID(request.getObjectID())
                .build();
    ***REMOVED***

//    @GetMapping(value = "/api/powershell")
//    public void executePowerShell() {
//        powerShellExecutor.execute("DISABLE", Arrays.asList("panokiv"));
//    ***REMOVED***

***REMOVED***
