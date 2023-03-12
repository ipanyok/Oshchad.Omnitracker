package ua.datastech.omnitracker.service.tracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OmniTrackerRequest;

import java.sql.PreparedStatement;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OmnitrackerService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void saveDataFromOmnitracker(OmniTrackerRequest request) {
        try {
            Thread.sleep(5000);
        ***REMOVED*** catch (InterruptedException e) {
            e.printStackTrace();
        ***REMOVED***
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("empNumber", request.getAdditionalInfo().getEmpNumber());

        List<Long> ids = jdbcTemplate.query("select * from usr where USR_EMP_NO = :empNumber", namedParameters, (rs, rowNum) ->
                rs.getLong("USR_KEY"));

        if (ids.isEmpty()) {
            log.info("User [empNumber=" + request.getAdditionalInfo().getEmpNumber() + "] wasn't found.");
        ***REMOVED*** else {
            namedParameters = new MapSqlParameterSource()
                    .addValue("id", ids.get(0))
                    .addValue("mainBranch", request.getAdditionalInfo().getMainBranch())
                    .addValue("tmpBranch", request.getAdditionalInfo().getTmpBranch())
                    .addValue("startDate", java.sql.Date.valueOf(request.getAdditionalInfo().getStartDate().substring(0, request.getAdditionalInfo().getStartDate().indexOf("T"))))
                    .addValue("endDate", java.sql.Date.valueOf(request.getAdditionalInfo().getEndDate().substring(0, request.getAdditionalInfo().getEndDate().indexOf("T"))));
            jdbcTemplate.execute("update usr set " +
                    "USR_UDF_MAINBRANCH = :mainBranch, " +
                    "USR_UDF_TEMPBRANCH = :tmpBranch, " +
                    "USR_UDF_REBRANCHINGSTARTDATE = :startDate, " +
                    "USR_UDF_REBRANCHINGENDDATE = :endDate " +
                    "WHERE USR_KEY = :id", namedParameters, PreparedStatement::executeUpdate
            );

            log.info("User [empNumber=" + request.getAdditionalInfo().getEmpNumber() + "] was saved.");
        ***REMOVED***
    ***REMOVED***


***REMOVED***
