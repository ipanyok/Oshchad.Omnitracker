package ua.datastech.omnitracker.service.tracker.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;

import java.sql.PreparedStatement;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class OmnitrackerApiLocal implements OmnitrackerApiService {


    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void callOmniTrackerClosureService(OimUserDto oimUserDto, ResponseCodeEnum closureCode, String solution, String solutionSpecification) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("empNumber", oimUserDto.getEmpNumber())
                .addValue("objectId", oimUserDto.getObjectId());
        jdbcTemplate.execute("update OMNI_REQUEST " +
                "set IS_CLOSURE_SENT = 1 " +
                "WHERE EMP_NO = :empNumber AND OBJECT_ID = :objectId", params, PreparedStatement::executeUpdate
        );
        log.info("callOmniTrackerClosureService()...");
    ***REMOVED***

    @Override
    public void callOmniTrackerPickupService(OimUserDto oimUserDto) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("empNumber", oimUserDto.getEmpNumber())
                .addValue("objectId", oimUserDto.getObjectId());
        jdbcTemplate.execute("update OMNI_REQUEST " +
                "set IS_PICKUP_SENT = 1 " +
                "WHERE EMP_NO = :empNumber AND OBJECT_ID = :objectId", params, PreparedStatement::executeUpdate
        );
        log.info("callOmniTrackerPickupService()...");
    ***REMOVED***
***REMOVED***
