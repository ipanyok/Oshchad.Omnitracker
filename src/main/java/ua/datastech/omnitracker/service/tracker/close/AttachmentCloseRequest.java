package ua.datastech.omnitracker.service.tracker.close;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ua.datastech.omnitracker.model.dto.ActionType;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.tracker.api.OmnitrackerApiService;

import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class AttachmentCloseRequest implements CloseRequest {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OmnitrackerApiService omnitrackerApiService;

    @Override
    // todo need refactor to avoid duplicate code and think how to close request (maybe in OmnitrackerService.closeRequest())
    public void cleanup(OimUserDto oimUserDto) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", oimUserDto.getId());
        Integer removedCount = jdbcTemplate.execute("delete from OMNI_BLOCK_ATTACHMENT where OMNI_BLOCK_REQUEST_ID = :id",
                namedParameters,
                PreparedStatement::executeUpdate
        );
        if (removedCount != null && removedCount != 0) {
            log.info("Omni block request data for request " + oimUserDto.getObjectId() + " was removed.");
        ***REMOVED***

        if (!oimUserDto.getIsPickupSent()) {
            omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
        ***REMOVED***
        omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Обробка звернення завершена за ініціативою Банка.", "");


        Integer execute = updateMainRequest(oimUserDto.getId(), jdbcTemplate);
        if (execute != 0) {
            log.info("Omni block request " + oimUserDto.getObjectId() + " was closed.");
        ***REMOVED***
    ***REMOVED***

    @Override
    public Set<String> getActions() {
        Set<String> actions = new HashSet<>();
        actions.add(ActionType.DISABLE_BY_FILE.name());
        actions.add(ActionType.ENABLE_BY_FILE.name());
        return actions;
    ***REMOVED***
***REMOVED***
