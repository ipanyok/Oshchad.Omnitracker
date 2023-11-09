package ua.datastech.omnitracker.service.tracker.api;

import com.ibm.icu.text.Transliterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ua.datastech.omnitracker.model.omni.api.OimClosureRequest;
import ua.datastech.omnitracker.model.omni.api.OimPickupRequest;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"prod", "test"})
public class OmnitrackerApi implements OmnitrackerApiService {

    @Value("${omnitracker_closure_url}")
    private String omniClosureUrl;

    @Value("${omnitracker_pickup_url}")
    private String omniPickupUrl;

    @Value("${omnitracker_attachment_url}")
    private String omniGetAttachmentUrl;

    @Value("${omnitracker_user}")
    private String omniUser;

    @Value("${omnitracker_password}")
    private String omniPassword;

    @Value("Cyrillic-Latin; Latin-ASCII")
    private String transliterator;

    private final RestTemplate restTemplate;

    private final JdbcQueryService jdbcQueryService;

    @Override
    public void callOmniTrackerPickupService(String empNumber, String objectId) {
        log.info("send call-pickup-ack()... objectId: " + objectId);
        OimPickupRequest oimPickupRequest = setupOimPickupRequest(objectId, objectId);
        HttpEntity request = new HttpEntity<>(oimPickupRequest, createHeaders(omniUser, omniPassword));
        ResponseEntity<String> response = restTemplate.postForEntity(omniPickupUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Something went wrong. Status: " + response.getStatusCode());
            throw new RuntimeException("Error during send pickup request to omnitracker: " + response.getBody());
        }

        // todo refactor using request action

        if (empNumber != null) {
            jdbcQueryService.updateOmniRequestQuery(empNumber, objectId, Collections.singletonMap("IS_PICKUP_SENT", "1"));
        } else {
            jdbcQueryService.updateOmniBlockRequestQuery(objectId, Collections.singletonMap("IS_PICKUP_SENT", "1"));
        }
        log.info("Pickup for " + objectId + " request was sent. Status: " + response.getStatusCode());
    }

    @Override
    public String callOmniTrackerGetAttachmentService(Long oid, String objectId) {
        log.info("send call-get-attachment()... objectId: " + objectId);
        HttpHeaders headers = createHeaders(omniUser, omniPassword);
        HttpEntity requestEntity = new HttpEntity<>(headers);
        URI uri = UriComponentsBuilder
                .fromUri(URI.create(omniGetAttachmentUrl))
                .queryParam("OID", oid)
                .build()
                .toUri();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Something went wrong. Status: " + response.getStatusCode());
            throw new RuntimeException("Error during send gey-attachment request to omnitracker: " + response.getBody());
        }
        log.info("Get-attachment for " + objectId + " request was sent. Status: " + response.getStatusCode());
        restTemplate.getMessageConverters().remove(0);
        return response.getBody();
    }

    @Override
    public void callOmniTrackerClosureService(String empNumber, String objectId, ResponseCodeEnum closureCode, String solution, String solutionSpecification) {
        log.info("send call-closure-req()... objectId: " + objectId);
        OimClosureRequest oimClosureRequest = setupOimClosureRequest(objectId, objectId, closureCode, solution, solutionSpecification);
        HttpEntity request = new HttpEntity<>(oimClosureRequest, createHeaders(omniUser, omniPassword));
        ResponseEntity<String> response = restTemplate.postForEntity(omniClosureUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Something went wrong. Status: " + response.getStatusCode());
            throw new RuntimeException("Error during send closure request to omnitracker: " + response.getBody());
        }
        Map<String, String> params = Stream.of(new String[][] {
                { "IS_CLOSURE_SENT", "1" },
                { "CLOSE_REASON", "'" + Transliterator.getInstance(transliterator).transliterate(solution).replaceAll("'", "") + "'"},
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        if (empNumber != null) {
            jdbcQueryService.updateOmniRequestQuery(empNumber, objectId, params);
        } else {
            jdbcQueryService.updateOmniBlockRequestQuery(objectId, params);
        }
        log.info("Closure for " + objectId + " request was sent. Status: " + response.getStatusCode());
    }

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + encodedAuth;
            set("Authorization", authHeader);
            setContentType(MediaType.APPLICATION_JSON);
        }};
    }

    private OimClosureRequest setupOimClosureRequest(String externalID, String objectID, ResponseCodeEnum closureCode, String solution, String solutionSpecification) {
        return OimClosureRequest.builder()
                .externalID(externalID)
                .objectID(objectID)
                .closureCode(closureCode)
                .solution(solution)
                .solutionSpecification(solutionSpecification)
                .build();
    }

    private OimPickupRequest setupOimPickupRequest(String externalID, String objectID) {
        return OimPickupRequest.builder()
                .externalID(externalID)
                .objectID(objectID)
                .externalDeadline(LocalDateTime.now().plusHours(1))
                .responsible("panokiv@oschadbank.ua")
                .responsibleInfo("panokiv@oschadbank.ua")
                .build();
    }

}
