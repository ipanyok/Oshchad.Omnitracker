package ua.datastech.omnitracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OimUserDto;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerJob {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Scheduled(cron = "@daily")
    public void cleanupData() {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("endDate", LocalDate.now());


        List<OimUserDto> rebranchedUsers = jdbcTemplate.query("select USR_KEY, USR_EMP_NO from usr where USR_UDF_REBRANCHINGENDDATE = :endDate", namedParameters, (rs, rowNum) -> OimUserDto.builder()
                .id(rs.getLong("USR_KEY"))
                .empNumber(rs.getString("USR_EMP_NO"))
                .build());

        rebranchedUsers.forEach(oimUserDto -> {
            SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                    .addValue("id", oimUserDto.getId());
            Integer execute = jdbcTemplate.execute("update usr set " +
                    "USR_UDF_MAINBRANCH = null, " +
                    "USR_UDF_TEMPBRANCH = null, " +
                    "USR_UDF_REBRANCHINGSTARTDATE = null, " +
                    "USR_UDF_REBRANCHINGENDDATE = null " +
                    "WHERE USR_KEY = :id", namedParametersForUpdate, PreparedStatement::executeUpdate
            );
            if (execute != 0) {
                log.info("User[empNumber=" + oimUserDto.getEmpNumber() + "] was updated");
            ***REMOVED***
        ***REMOVED***);

    ***REMOVED***

***REMOVED***
