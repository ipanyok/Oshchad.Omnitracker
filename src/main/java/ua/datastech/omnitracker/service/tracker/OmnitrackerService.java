package ua.datastech.omnitracker.service.tracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OmniTrackerRequest;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void saveOmniRequest(OmniTrackerRequest request) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("objectId", request.getObjectID())
                .addValue("empNumber", request.getAdditionalInfo().getEmpNumber())
                .addValue("mainBranch", request.getAdditionalInfo().getMainBranch())
                .addValue("tmpBranch", request.getAdditionalInfo().getTmpBranch())
                .addValue("startDate", java.sql.Date.valueOf(request.getAdditionalInfo().getStartDate().substring(0, request.getAdditionalInfo().getStartDate().indexOf("T"))))
                .addValue("endDate", java.sql.Date.valueOf(request.getAdditionalInfo().getEndDate().substring(0, request.getAdditionalInfo().getEndDate().indexOf("T"))))
                .addValue("localDate", LocalDateTime.now());
        Integer execute = jdbcTemplate.execute("insert into omni_request (OBJECT_ID, EMP_NO, MAINBRANCH, TEMPBRANCH, REBRANCHINGSTARTDATE, REBRANCHINGENDDATE, CHANGED_AT) VALUES (:objectId, :empNumber, :mainBranch, :tmpBranch, :startDate, :endDate, :localDate)",
                namedParameters,
                PreparedStatement::executeUpdate
        );
        if (execute != 0) {
            log.info("Omni request " + request.getObjectID() + " was saved.");
        ***REMOVED***
    ***REMOVED***

***REMOVED***
