package ua.datastech.omnitracker.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.dto.ResponseCodeEnum;
import ua.datastech.omnitracker.service.tracker.OmnitrackerApiService;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerJob {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OmnitrackerApiService omnitrackerApiService;

    @Transactional
    @Scheduled(cron = "*/10 * * * * *")
    public void saveOmniDataToOIM() {
        List<OimUserDto> omniData = jdbcTemplate.query("select * from OMNI_REQUEST where IS_SAVED = 0 AND IS_PROCESSED = 0", (rs, rowNum) -> OimUserDto.builder()
                .objectId(rs.getString("OBJECT_ID"))
                .empNumber(rs.getString("EMP_NO"))
                .mainBranch(rs.getString("MAINBRANCH"))
                .tmpBranch(rs.getString("TEMPBRANCH"))
                .startDate(rs.getString("REBRANCHINGSTARTDATE"))
                .endDate(rs.getString("REBRANCHINGENDDATE"))
                .build());

        omniData.forEach(oimUserDto -> {
            omnitrackerApiService.callOmniTrackerPickupService(oimUserDto);

            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("empNumber", oimUserDto.getEmpNumber());

            List<Long> ids = jdbcTemplate.query("select * from usr where USR_EMP_NO = :empNumber", namedParameters, (rs, rowNum) ->
                    rs.getLong("USR_KEY"));

            if (ids.isEmpty()) {
                log.info("User [empNumber=" + oimUserDto.getEmpNumber() + "] wasn't found.");
                SqlParameterSource params = new MapSqlParameterSource()
                        .addValue("empNumber", oimUserDto.getEmpNumber())
                        .addValue("objectId", oimUserDto.getObjectId());
                jdbcTemplate.execute("update OMNI_REQUEST " +
                        "set IS_PROCESSED = 1 " +
                        "WHERE EMP_NO = :empNumber AND OBJECT_ID = :objectId", params, PreparedStatement::executeUpdate
                );
                omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getObjectId(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "REJECTED", "User [empNumber=" + oimUserDto.getEmpNumber() + "] wasn't found.");
            ***REMOVED*** else {
                SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                        .addValue("empNumber", oimUserDto.getEmpNumber())
                        .addValue("objectId", oimUserDto.getObjectId())
                        .addValue("mainBranch", oimUserDto.getMainBranch())
                        .addValue("tmpBranch", oimUserDto.getTmpBranch())
                        .addValue("startDate", java.sql.Date.valueOf(oimUserDto.getStartDate()))
                        .addValue("endDate", java.sql.Date.valueOf(oimUserDto.getEndDate()));
                Integer execute = jdbcTemplate.execute("update usr set " +
                        "USR_UDF_OBJECT_ID = :objectId, " +
                        "USR_UDF_MAINBRANCH = :mainBranch, " +
                        "USR_UDF_TEMPBRANCH = :tmpBranch, " +
                        "USR_UDF_REBRANCHINGSTARTDATE = :startDate, " +
                        "USR_UDF_REBRANCHINGENDDATE = :endDate " +
                        "WHERE USR_EMP_NO = :empNumber", namedParametersForUpdate, PreparedStatement::executeUpdate
                );
                if (execute != 0) {
                    namedParametersForUpdate = new MapSqlParameterSource()
                            .addValue("empNumber", oimUserDto.getEmpNumber())
                            .addValue("objectId", oimUserDto.getObjectId());
                    jdbcTemplate.execute("update OMNI_REQUEST " +
                            "set IS_SAVED = 1 " +
                            "WHERE EMP_NO = :empNumber AND OBJECT_ID = :objectId", namedParametersForUpdate, PreparedStatement::executeUpdate
                    );
                    log.info("User[empNumber=" + oimUserDto.getEmpNumber() + "] data was saved in OIM");
                ***REMOVED***
            ***REMOVED***
        ***REMOVED***);
    ***REMOVED***

    @Transactional
    @Scheduled(cron = "*/10 * * * * *")
    public void processRebranching() {
        List<OimUserDto> rebranchedUsers = jdbcTemplate.query("select USR_KEY, USR_EMP_NO, USR_UDF_OBJECT_ID from usr where USR_UDF_OBJECT_ID is not null", (rs, rowNum) -> OimUserDto.builder()
                .usrKey(rs.getLong("USR_KEY"))
                .empNumber(rs.getString("USR_EMP_NO"))
                .objectId(rs.getString("USR_UDF_OBJECT_ID"))
                .build());

        rebranchedUsers.forEach(oimUserDto -> {
            // todo do something
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("empNumber", oimUserDto.getEmpNumber())
                    .addValue("objectId", oimUserDto.getObjectId());

            List<OimUserDto> omniData = jdbcTemplate.query("select * from OMNI_REQUEST where IS_PROCESSED = 0 and IS_SAVED = 1 and EMP_NO = :empNumber AND OBJECT_ID = :objectId", namedParameters, (rs, rowNum) -> OimUserDto.builder()
                    .objectId(rs.getString("OBJECT_ID"))
                    .empNumber(rs.getString("EMP_NO"))
                    .mainBranch(rs.getString("MAINBRANCH"))
                    .tmpBranch(rs.getString("TEMPBRANCH"))
                    .startDate(rs.getString("REBRANCHINGSTARTDATE"))
                    .endDate(rs.getString("REBRANCHINGENDDATE"))
                    .build());

            omniData.forEach(o -> {

                omnitrackerApiService.callOmniTrackerClosureService(o.getObjectId(), o.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, "RESOLVED", "");

                jdbcTemplate.execute("update OMNI_REQUEST " +
                        "set IS_PROCESSED = 1 " +
                        "WHERE EMP_NO = :empNumber AND OBJECT_ID = :objectId", namedParameters, PreparedStatement::executeUpdate
                );
            ***REMOVED***);

        ***REMOVED***);

    ***REMOVED***

    @Scheduled(cron = "@daily")
    public void cleanupData() {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("endDate", LocalDate.now());

        List<OimUserDto> rebranchedUsers = jdbcTemplate.query("select USR_KEY, USR_EMP_NO from usr where USR_UDF_REBRANCHINGENDDATE = :endDate", namedParameters, (rs, rowNum) -> OimUserDto.builder()
                .usrKey(rs.getLong("USR_KEY"))
                .empNumber(rs.getString("USR_EMP_NO"))
                .build());

        rebranchedUsers.forEach(oimUserDto -> {
            SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                    .addValue("usrKey", oimUserDto.getUsrKey());
            Integer execute = jdbcTemplate.execute("update usr set " +
                    "USR_UDF_OBJECT_ID = null, " +
                    "USR_UDF_MAINBRANCH = null, " +
                    "USR_UDF_TEMPBRANCH = null, " +
                    "USR_UDF_REBRANCHINGSTARTDATE = null, " +
                    "USR_UDF_REBRANCHINGENDDATE = null " +
                    "WHERE USR_KEY = :usrKey", namedParametersForUpdate, PreparedStatement::executeUpdate
            );
            if (execute != 0) {
                log.info("User[empNumber=" + oimUserDto.getEmpNumber() + "] was cleaned up");
            ***REMOVED***
        ***REMOVED***);

    ***REMOVED***

***REMOVED***
