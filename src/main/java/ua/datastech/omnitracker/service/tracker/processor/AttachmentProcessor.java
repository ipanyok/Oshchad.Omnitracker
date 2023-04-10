package ua.datastech.omnitracker.service.tracker.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ua.datastech.omnitracker.model.oim.OmniTrackerRequest;

import java.util.HashSet;
import java.util.Set;

import static ua.datastech.omnitracker.model.dto.ActionType.DISABLE_BY_FILE;
import static ua.datastech.omnitracker.model.dto.ActionType.ENABLE_BY_FILE;


@Component
@Slf4j
@RequiredArgsConstructor
public class AttachmentProcessor implements OmniRequestProcessor {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void process(OmniTrackerRequest request) {
        Integer execute = saveOmniBlockRequest(request, jdbcTemplate);
        if (execute != 0) {
            log.info("Omni block request " + request.getObjectID() + " was saved.");
        ***REMOVED***
    ***REMOVED***

    @Override
    public Set<String> getActions() {
        Set<String> actionTypes = new HashSet<>();
        actionTypes.add(DISABLE_BY_FILE.name());
        actionTypes.add(ENABLE_BY_FILE.name());
        return actionTypes;
    ***REMOVED***
***REMOVED***
