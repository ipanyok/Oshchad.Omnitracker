package ua.datastech.omnitracker.service.tracker.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.datastech.omnitracker.model.omni.api.OimClosureRequest;
import ua.datastech.omnitracker.model.omni.api.OimPickupRequest;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Profile("prod")
@Service
@RequiredArgsConstructor
@Slf4j
public class OmnitrackerApi implements OmnitrackerApiService {

    @Value("${omnitracker_closure_url***REMOVED***")
    private String omniClosureUrl;

    @Value("${omnitracker_pickup_url***REMOVED***")
    private String omniPickupUrl;

    @Value("${omnitracker_user***REMOVED***")
    private String omniUser;

    @Value("${omnitracker_password***REMOVED***")
    private String omniPassword;

    private final RestTemplate restTemplate;

    @Override
    public void callOmniTrackerPickupService(OimUserDto oimUserDto) {
        log.info("call-pickup-ack()... objectId: " + oimUserDto.getObjectId());
        OimPickupRequest oimPickupRequest = setupOimPickupRequest(oimUserDto.getObjectId(), oimUserDto.getObjectId());
        HttpEntity<String> request = new HttpEntity<>(oimPickupRequest.toString(), createHeaders(omniUser, omniPassword));
        ResponseEntity<String> response = restTemplate.postForEntity(omniPickupUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Something went wrong.");
            throw new RuntimeException("Error during send pickup request to omnitracker: " + response.getBody());
        ***REMOVED***
         log.info("Pickup for " + oimUserDto.getObjectId() + " request was sent");
    ***REMOVED***

    @Override
    public void callOmniTrackerClosureService(String externalID, String objectID, ResponseCodeEnum closureCode, String solution, String solutionSpecification) {
        log.info("call-closure-req()... objectId: " + objectID);
        OimClosureRequest oimClosureRequest = setupOimClosureRequest(externalID, objectID, closureCode, solution, solutionSpecification);
        HttpEntity<String> request = new HttpEntity<>(oimClosureRequest.toString(), createHeaders(omniUser, omniPassword));
        ResponseEntity<String> response = restTemplate.postForEntity(omniClosureUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Something went wrong.");
            throw new RuntimeException("Error during send closure request to omnitracker: " + response.getBody());
        ***REMOVED***
        log.info("Closure for " + objectID + " request was sent");
    ***REMOVED***

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
            setContentType(MediaType.APPLICATION_JSON);
        ***REMOVED******REMOVED***;
    ***REMOVED***

    private OimClosureRequest setupOimClosureRequest(String externalID, String objectID, ResponseCodeEnum closureCode, String solution, String solutionSpecification) {
        return OimClosureRequest.builder()
                .externalID(externalID)
                .objectID(objectID)
                .closureCode(closureCode)
                .solution(solution)
                .solutionSpecification(solutionSpecification)
                .build();
    ***REMOVED***

    private OimPickupRequest setupOimPickupRequest(String externalID, String objectID) {
        return OimPickupRequest.builder()
                .externalID(externalID)
                .objectID(objectID)
                .externalDeadline(LocalDateTime.now().plusHours(1))
                .responsible("panokiv@oschadbank.ua")
                .responsibleInfo("panokiv@oschadbank.ua")
                .build();
    ***REMOVED***

***REMOVED***
