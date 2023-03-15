package ua.datastech.omnitracker.service.tracker.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.OimClosureRequest;
import ua.datastech.omnitracker.model.omni.api.OimPickupRequest;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Base64;

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

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void callOmniTrackerPickupService(OimUserDto oimUserDto) {
        log.info("call-pickup-ack()... objectId: " + oimUserDto.getObjectId());
        OimPickupRequest oimPickupRequest = setupOimPickupRequest(oimUserDto.getObjectId(), oimUserDto.getObjectId());
        HttpEntity request = new HttpEntity<>(oimPickupRequest, createHeaders(omniUser, omniPassword));
        ResponseEntity<String> response = restTemplate.postForEntity(omniPickupUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK  ) {
            log.error("Something went wrong. Status: " + response.getStatusCode());
            throw new RuntimeException("Error during send pickup request to omnitracker: " + response.getBody());
        ***REMOVED***
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("empNumber", oimUserDto.getEmpNumber())
                .addValue("objectId", oimUserDto.getObjectId());
        jdbcTemplate.execute("update OMNI_REQUEST " +
                "set IS_PICKUP_SENT = 1 " +
                "WHERE EMP_NO = :empNumber AND OBJECT_ID = :objectId", params, PreparedStatement::executeUpdate
        );
        log.info("Pickup for " + oimUserDto.getObjectId() + " request was sent. Status: " + response.getStatusCode());
    ***REMOVED***

    @Override
    public void callOmniTrackerClosureService(OimUserDto oimUserDto, ResponseCodeEnum closureCode, String solution, String solutionSpecification) {
        log.info("call-closure-req()... objectId: " + oimUserDto.getObjectId());
        OimClosureRequest oimClosureRequest = setupOimClosureRequest(oimUserDto.getObjectId(), oimUserDto.getObjectId(), closureCode, solution, solutionSpecification);
        HttpEntity request = new HttpEntity<>(oimClosureRequest, createHeaders(omniUser, omniPassword));
        ResponseEntity<String> response = restTemplate.postForEntity(omniClosureUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Something went wrong. Status: " + response.getStatusCode());
            throw new RuntimeException("Error during send closure request to omnitracker: " + response.getBody());
        ***REMOVED***
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("empNumber", oimUserDto.getEmpNumber())
                .addValue("objectId", oimUserDto.getObjectId());
        jdbcTemplate.execute("update OMNI_REQUEST " +
                "set IS_CLOSURE_SENT = 1 " +
                "WHERE EMP_NO = :empNumber AND OBJECT_ID = :objectId", params, PreparedStatement::executeUpdate
        );
        log.info("Closure for " + oimUserDto.getObjectId() + " request was sent. Status: " + response.getStatusCode());
    ***REMOVED***

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + encodedAuth;
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
