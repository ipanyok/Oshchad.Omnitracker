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
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.tracker.api.OmnitrackerApiService;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerJob {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OmnitrackerApiService omnitrackerApiService;

    // todo think about transactions
//    @Transactional
    @Scheduled(cron = "*/10 * * * * *") // todo 10 min
    public void saveOmniDataToOIM() {
        List<OimUserDto> omniData = jdbcTemplate.query("select * from OMNI_REQUEST where IS_SAVED = 0 AND IS_PROCESSED = 0", (rs, rowNum) -> OimUserDto.builder()
                .objectId(rs.getString("OBJECT_ID"))
                .empNumber(rs.getString("EMP_NO"))
                .mainBranch(rs.getString("MAINBRANCH"))
                .tmpBranch(rs.getString("TEMPBRANCH"))
                .startDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("REBRANCHINGSTARTDATE")))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("REBRANCHINGENDDATE")))
                .isPickupSent(rs.getBoolean("IS_PICKUP_SENT"))
                .isClosureSent(rs.getBoolean("IS_CLOSURE_SENT"))
                .build());

        omniData.forEach(oimUserDto -> {
            if (!oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerPickupService(oimUserDto);
            ***REMOVED***

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
                if (!oimUserDto.getIsClosureSent()) {
                    omnitrackerApiService.callOmniTrackerClosureService(oimUserDto, ResponseCodeEnum.SC_CC_REJECTED, "Відмовлено", "Користувач [empNumber=" + oimUserDto.getEmpNumber() + "] не знайдений в системі ОІМ.");
                ***REMOVED***
            ***REMOVED*** else {
                SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                        .addValue("empNumber", oimUserDto.getEmpNumber())
                        .addValue("objectId", oimUserDto.getObjectId())
                        .addValue("mainBranch", oimUserDto.getMainBranch())
                        .addValue("tmpBranch", oimUserDto.getTmpBranch())
                        .addValue("startDate", java.sql.Date.valueOf(oimUserDto.getStartDate()))
                        .addValue("endDate", java.sql.Date.valueOf(oimUserDto.getEndDate()));
                Integer execute = jdbcTemplate.execute("update usr set " +
                        "USR_UDF_OBJECTID = :objectId, " +
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
    @Scheduled(cron = "*/10 * * * * *") // todo 10 min
    public void processRebranching() {
        List<OimUserDto> rebranchedUsers = jdbcTemplate.query("select USR_KEY, USR_EMP_NO, USR_UDF_OBJECTID from usr where USR_UDF_OBJECTID is not null", (rs, rowNum) -> OimUserDto.builder()
                .usrKey(rs.getLong("USR_KEY"))
                .empNumber(rs.getString("USR_EMP_NO"))
                .objectId(rs.getString("USR_UDF_OBJECTID"))
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
                    .startDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("REBRANCHINGSTARTDATE")))
                    .endDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("REBRANCHINGENDDATE")))
                    .isPickupSent(rs.getBoolean("IS_PICKUP_SENT"))
                    .isClosureSent(rs.getBoolean("IS_CLOSURE_SENT"))
                    .build());

            omniData.forEach(o -> {

                if (!o.getIsClosureSent()) {
                    omnitrackerApiService.callOmniTrackerClosureService(o, ResponseCodeEnum.SC_CC_RESOLVED, "Вирішено", "");
                ***REMOVED***

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
                    "USR_UDF_OBJECTID = null, " +
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
