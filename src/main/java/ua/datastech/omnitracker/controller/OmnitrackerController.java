package ua.datastech.omnitracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.datastech.omnitracker.model.dto.OmniTrackerRequest;
import ua.datastech.omnitracker.model.dto.OmniTrackerResponse;
import ua.datastech.omnitracker.service.tracker.OmnitrackerService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerController {

    private final OmnitrackerService omnitrackerService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);


    @PostMapping(value = "/api/call-dispatch-req", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OmniTrackerResponse omnitrackerApi(@RequestBody OmniTrackerRequest request) {
        executorService.execute(() -> omnitrackerService.saveDataFromOmnitracker(request));
        executorService.shutdown();
        return OmniTrackerResponse.builder()
                .externalID(request.getObjectID())
                .build();
    ***REMOVED***

***REMOVED***
