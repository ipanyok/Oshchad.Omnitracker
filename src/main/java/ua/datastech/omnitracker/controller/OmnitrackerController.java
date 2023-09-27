package ua.datastech.omnitracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.datastech.omnitracker.model.oim.OmniTrackerAttachmentInfoRequest;
import ua.datastech.omnitracker.model.oim.OmniTrackerRequest;
import ua.datastech.omnitracker.model.omni.api.OmniTrackerResponse;
import ua.datastech.omnitracker.service.tracker.OmnitrackerService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerController {

    private final OmnitrackerService omnitrackerService;

    @PostMapping(value = "/api/call-dispatch-req", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OmniTrackerResponse omnitrackerApiBlocking(@RequestBody OmniTrackerRequest request) {
        log.info("receive call-dispatch-req()... objectId: " + request.getObjectID());
        omnitrackerService.saveOmniBlockRequest(request);
        return OmniTrackerResponse.builder()
                .externalID(request.getObjectID())
                .build();
    }

    @PostMapping(value = "/api/call-user-info", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OmniTrackerResponse omnitrackerApiGetAttachmentsInfo(@RequestBody OmniTrackerAttachmentInfoRequest request) {
        log.info("receive call-update-req()... objectId: " + request.getObjectID());
        omnitrackerService.saveOmniAttachmentRequest(request);
        return OmniTrackerResponse.builder()
                .externalID(request.getObjectID())
                .build();
    }

    @PostMapping(value = "/api/call-closure-ack", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity omnitrackerApiClosureAck(@RequestBody String request) {
        log.info("receive call-closure-ack()");
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/api/call-cancel-req", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity omnitrackerApiCancelRequest(@RequestBody String request) {
        log.info("receive call-cancel-req()");
        return new ResponseEntity(HttpStatus.OK);
    }

}
