package ua.datastech.omnitracker.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OimUserDto;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JdbcQueryService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final static String OMNI_UPDATE_QUERY = "update OMNI_REQUEST " +
            "set %s " +
            "WHERE EMP_NO = :empNumber AND OBJECT_ID = :objectId";

    private final static String OMNI_UPDATE_BLOCK_QUERY = "update OMNI_BLOCK_REQUEST " +
            "set %s " +
            "WHERE OBJECT_ID = :objectId";

    private final static String OMNI_FIND_ALL_UNPROCESSED_REQUESTS_QUERY = "select * from OMNI_REQUEST where IS_SAVED = 0 AND IS_PROCESSED = 0";

    private final static String OMNI_FIND_ALL_BLOCK_UNPROCESSED_REQUESTS_QUERY = "select * " +
            "from OMNI_BLOCK_REQUEST B, OMNI_BLOCK_DATA D " +
            "where B.ID = D.OMNI_BLOCK_REQUEST_ID " +
            "AND IS_PROCESSED = 0";

    private final static String OMNI_FIND_ALL_BLOCK_ATTACHMENT_QUERY = "select * " +
            "from OMNI_BLOCK_REQUEST B, OMNI_BLOCK_ATTACHMENT D " +
            "where B.ID = D.OMNI_BLOCK_REQUEST_ID " +
            "AND IS_PROCESSED = 0";


    private final static String OMNI_FIND_USER_TO_BLOCK_QUERY = "select * " +
            "from OMNI_BLOCK_REQUEST B, OMNI_BLOCK_DATA D " +
            "where B.ID = D.OMNI_BLOCK_REQUEST_ID " +
            "AND IS_PROCESSED = 0 AND ACTION_DATE = :actionDate";

    private final static String OMNI_FIND_USER_TO_BLOCK_ATTACHMENT_QUERY = "select * " +
            "from OMNI_BLOCK_REQUEST B, OMNI_BLOCK_ATTACHMENT D " +
            "where B.ID = D.OMNI_BLOCK_REQUEST_ID " +
            "AND IS_PROCESSED = 0 AND ACTION_DATE = :actionDate";

    private final static String OMNI_FIND_ONLY_UNPROCESSED_REQUESTS_QUERY = "select * from OMNI_REQUEST where IS_PROCESSED = 0 and IS_SAVED = 1 and EMP_NO = :empNumber AND OBJECT_ID = :objectId";

    private final static String OIM_FIND_USER_BY_EMP_NUMBER_QUERY = "select * from usr where USR_EMP_NO = :empNumber";

    private final static String OIM_UPDATE_USER_BY_EMP_NUMBER_QUERY = "update usr set " +
            "USR_UDF_OBJECTID = :objectId, " +
            "USR_UDF_MAINBRANCH = :mainBranch, " +
            "USR_UDF_TEMPBRANCH = :tmpBranch, " +
            "USR_UDF_REBRANCHINGSTARTDATE = :startDate, " +
            "USR_UDF_REBRANCHINGENDDATE = :endDate " +
            "WHERE USR_EMP_NO = :empNumber";

    private final static String OMNI_UPDATE_ATTACHMENT_QUERY = "update OMNI_BLOCK_ATTACHMENT set " +
            "ATTACHMENT = :attachment " +
            "WHERE OMNI_BLOCK_REQUEST_ID = :omniBlockRequestId";

    private final static String OIM_FIND_UNPROCESSED_USERS_QUERY = "select USR_KEY, USR_EMP_NO, USR_UDF_OBJECTID from usr where USR_UDF_OBJECTID is not null";

    private final static String OIM_FIND_USERS_TO_CLEAN_QUERY = "select USR_KEY, USR_EMP_NO from usr where USR_UDF_REBRANCHINGENDDATE = :endDate";

    public void updateOmniRequestQuery(String empNumber, String objectId, Map<String, String> valuesToUpdate) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("empNumber", empNumber)
                .addValue("objectId", objectId);
        jdbcTemplate.execute(String.format(OMNI_UPDATE_QUERY, convertParamsMapToQueryData(valuesToUpdate)), params, PreparedStatement::executeUpdate);
    ***REMOVED***

    public void updateOmniBlockRequestQuery(String objectId, Map<String, String> valuesToUpdate) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("objectId", objectId);
        jdbcTemplate.execute(String.format(OMNI_UPDATE_BLOCK_QUERY, convertParamsMapToQueryData(valuesToUpdate)), params, PreparedStatement::executeUpdate);
    ***REMOVED***

    public List<OimUserDto> findAllUnprocessedRequests() {
        return jdbcTemplate.query(OMNI_FIND_ALL_UNPROCESSED_REQUESTS_QUERY, (rs, rowNum) -> OimUserDto.builder()
                .objectId(rs.getString("OBJECT_ID"))
                .empNumber(rs.getString("EMP_NO"))
                .mainBranch(rs.getString("MAINBRANCH"))
                .tmpBranch(rs.getString("TEMPBRANCH"))
                .startDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("REBRANCHINGSTARTDATE")))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("REBRANCHINGENDDATE")))
                .isPickupSent(rs.getBoolean("IS_PICKUP_SENT"))
                .isClosureSent(rs.getBoolean("IS_CLOSURE_SENT"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findAllUnprocessedBlockRequests() {
        return jdbcTemplate.query(OMNI_FIND_ALL_BLOCK_UNPROCESSED_REQUESTS_QUERY, (rs, rowNum) -> OimUserDto.builder()
                .objectId(rs.getString("OBJECT_ID"))
                .adLogin(rs.getString("AD_LOGIN"))
                .action(rs.getString("ACTION"))
                .actionDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("ACTION_DATE")))
                .isPickupSent(rs.getBoolean("IS_PICKUP_SENT"))
                .isClosureSent(rs.getBoolean("IS_CLOSURE_SENT"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findAllUnprocessedAttachmentsRequests() {
        return jdbcTemplate.query(OMNI_FIND_ALL_BLOCK_ATTACHMENT_QUERY, (rs, rowNum) -> OimUserDto.builder()
                .objectId(rs.getString("OBJECT_ID"))
                .oid(rs.getLong("OID"))
                .isPickupSent(rs.getBoolean("IS_PICKUP_SENT"))
                .id(rs.getLong("ID"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findUsersToBlock(LocalDate date) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("actionDate", date);
        return jdbcTemplate.query(OMNI_FIND_USER_TO_BLOCK_QUERY, namedParameters, (rs, rowNum) -> OimUserDto.builder()
                .adLogin(rs.getString("AD_LOGIN"))
                .objectId(rs.getString("OBJECT_ID"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findAttachment(LocalDate date) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("actionDate", date);
        return jdbcTemplate.query(OMNI_FIND_USER_TO_BLOCK_ATTACHMENT_QUERY, namedParameters, (rs, rowNum) -> OimUserDto.builder()
                .attachment(rs.getString("ATTACHMENT"))
                .objectId(rs.getString("OBJECT_ID"))
                .build());
    ***REMOVED***

    public List<Long> findOimUserByEmpNumber(String empNumber) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("empNumber", empNumber);

        return jdbcTemplate.query(OIM_FIND_USER_BY_EMP_NUMBER_QUERY, namedParameters, (rs, rowNum) ->
                rs.getLong("USR_KEY"));
    ***REMOVED***

    public Integer updateOimUser(OimUserDto oimUserDto) {
        SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                .addValue("empNumber", oimUserDto.getEmpNumber())
                .addValue("objectId", oimUserDto.getObjectId())
                .addValue("mainBranch", oimUserDto.getMainBranch())
                .addValue("tmpBranch", oimUserDto.getTmpBranch())
                .addValue("startDate", java.sql.Date.valueOf(oimUserDto.getStartDate()))
                .addValue("endDate", java.sql.Date.valueOf(oimUserDto.getEndDate()));
        return jdbcTemplate.execute(OIM_UPDATE_USER_BY_EMP_NUMBER_QUERY, namedParametersForUpdate, PreparedStatement::executeUpdate);
    ***REMOVED***

    public Integer updateAttachments(Long objectId, String attachment) {
        SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                .addValue("omniBlockRequestId", objectId)
                .addValue("attachment", attachment);
        return jdbcTemplate.execute(OMNI_UPDATE_ATTACHMENT_QUERY, namedParametersForUpdate, PreparedStatement::executeUpdate);
    ***REMOVED***

    public List<OimUserDto> findOimUnprocessedUsers() {
        return jdbcTemplate.query(OIM_FIND_UNPROCESSED_USERS_QUERY, (rs, rowNum) -> OimUserDto.builder()
                .usrKey(rs.getLong("USR_KEY"))
                .empNumber(rs.getString("USR_EMP_NO"))
                .objectId(rs.getString("USR_UDF_OBJECTID"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findOmniUnprocessedRequests(String empNumber, String objectId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("empNumber", empNumber)
                .addValue("objectId", objectId);

        return jdbcTemplate.query(OMNI_FIND_ONLY_UNPROCESSED_REQUESTS_QUERY, namedParameters, (rs, rowNum) -> OimUserDto.builder()
                .objectId(rs.getString("OBJECT_ID"))
                .empNumber(rs.getString("EMP_NO"))
                .mainBranch(rs.getString("MAINBRANCH"))
                .tmpBranch(rs.getString("TEMPBRANCH"))
                .startDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("REBRANCHINGSTARTDATE")))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("REBRANCHINGENDDATE")))
                .isPickupSent(rs.getBoolean("IS_PICKUP_SENT"))
                .isClosureSent(rs.getBoolean("IS_CLOSURE_SENT"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findOimUsersToClean() {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("endDate", LocalDate.now());

        return jdbcTemplate.query(OIM_FIND_USERS_TO_CLEAN_QUERY, namedParameters, (rs, rowNum) -> OimUserDto.builder()
                .usrKey(rs.getLong("USR_KEY"))
                .empNumber(rs.getString("USR_EMP_NO"))
                .build());
    ***REMOVED***

    public Integer updateOimUserByUsrKey(Long usrKey) {
        SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                .addValue("usrKey", usrKey);
        return jdbcTemplate.execute("update usr set " +
                "USR_UDF_OBJECTID = null, " +
                "USR_UDF_MAINBRANCH = null, " +
                "USR_UDF_TEMPBRANCH = null, " +
                "USR_UDF_REBRANCHINGSTARTDATE = null, " +
                "USR_UDF_REBRANCHINGENDDATE = null " +
                "WHERE USR_KEY = :usrKey", namedParametersForUpdate, PreparedStatement::executeUpdate
        );
    ***REMOVED***

    // todo to remove (it's for testing)
    public Integer updateOimUserEndDate(String empNumber) {
//        throw new RuntimeException("Error");
        SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                .addValue("empNumber", empNumber);
        return jdbcTemplate.execute("update usr set " +
                "USR_UDF_REBRANCHINGENDDATE = null " +
                "WHERE USR_EMP_NO = :empNumber", namedParametersForUpdate, PreparedStatement::executeUpdate
        );
    ***REMOVED***

    private String convertParamsMapToQueryData(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining(", "));
    ***REMOVED***

***REMOVED***
