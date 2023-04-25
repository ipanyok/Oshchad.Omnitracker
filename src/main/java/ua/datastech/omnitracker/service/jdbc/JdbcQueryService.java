package ua.datastech.omnitracker.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.oim.ProcessedUser;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

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
            "from OMNI_BLOCK_REQUEST " +
            "WHERE IS_PROCESSED = 0 " +
            "AND ACTION IN ('DISABLE_USER', 'ENABLE_USER', 'DISABLE_REGION', 'ENABLE_REGION')";

    private final static String OMNI_FIND_ALL_BLOCK_ATTACHMENT_QUERY = "select * " +
            "from OMNI_BLOCK_REQUEST " +
            "WHERE IS_PROCESSED = 0 " +
            "AND ACTION IN ('DISABLE_BY_FILE', 'ENABLE_BY_FILE')";


    private final static String OMNI_FIND_USER_TO_BLOCK_QUERY = "select * " +
            "from OMNI_BLOCK_REQUEST B, OMNI_BLOCK_DATA D " +
            "where B.ID = D.OMNI_BLOCK_REQUEST_ID " +
            "AND IS_PROCESSED = 0 AND ACTION_DATE = :actionDate AND OBJECT_ID = :objectId";

    private final static String OMNI_FIND_USER_TO_BLOCK_ATTACHMENT_QUERY = "select * " +
            "from OMNI_BLOCK_REQUEST B, OMNI_BLOCK_ATTACHMENT D " +
            "where B.ID = D.OMNI_BLOCK_REQUEST_ID " +
            "AND IS_PROCESSED = 0 AND ACTION_DATE = :actionDate AND OBJECT_ID = :objectId";

    private final static String OMNI_FIND_ATTACHMENT_QUERY = "select d.id attachment_id, d.*, b.* " +
            "from OMNI_BLOCK_REQUEST B, OMNI_BLOCK_ATTACHMENT D " +
            "where B.ID = D.OMNI_BLOCK_REQUEST_ID " +
            "AND IS_PROCESSED = 0 AND OBJECT_ID = :objectId";

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
            "WHERE ID = :id";

    private final static String OIM_FIND_UNPROCESSED_USERS_QUERY = "select USR_KEY, USR_EMP_NO, USR_UDF_OBJECTID from usr where USR_UDF_OBJECTID is not null";

    private final static String OIM_FIND_USERS_TO_CLEAN_QUERY = "select USR_KEY, USR_EMP_NO from usr where USR_UDF_REBRANCHINGENDDATE = :endDate";

    private final static String OIM_FIND_USR_TO_BLOCK_BY_SOURCE_IF_EQUAL_QUERY = "SELECT distinct ad.ud_ADUSER_UID AD_LOGIN, 'UPDATE USR SET USR_STATUS = ''Disabled'' where usr_key = '''||usr.usr_key||'''' disable_usr\n" +
            "FROM USR\n" +
            "  JOIN ORC on orc.usr_key = usr.usr_key\n" +
            "  join UD_ADUSER ad on ad.ORC_KEY = orc.orc_key\n" +
            "  join oiu on oiu .orc_key = orc.orc_key\n" +
            "  join ost on oiu.ost_key = ost.osT_key and OST_STATUS IN ('Disabled', 'Provisioned', 'Enabled')\n" +
            "WHERE USR.USR_DISPLAY_NAME NOT IN (select LKV_DECODED from LKu \n" +
            "    join lkv on lku.lku_key = lkv.lku_key \n" +
            "    where LKU_TYPE_STRING_KEY = 'Lookup.Actualize.UserVacationExclusion') \n" +
            "AND usr.usr_key IN (SELECT usr_key from usr where USR_UDF_OL_0_2 = (SELECT ORG_UDF_HRORGNAME FROM ACT where ORG_UDF_HRORGCODE = :sourceId))";

    private final static String OIM_FIND_USR_TO_BLOCK_BY_SOURCE_IF_NOT_EQUAL_QUERY = "SELECT distinct ad.ud_ADUSER_UID AD_LOGIN, 'UPDATE USR SET USR_STATUS = ''Disabled'' where usr_key = '''||usr.usr_key||'''' disable_usr\n" +
            "FROM USR\n" +
            "  JOIN ORC on orc.usr_key = usr.usr_key\n" +
            "  join UD_ADUSER ad on ad.ORC_KEY = orc.orc_key\n" +
            "  join oiu on oiu .orc_key = orc.orc_key\n" +
            "  join ost on oiu.ost_key = ost.osT_key and OST_STATUS IN ('Disabled', 'Provisioned', 'Enabled')\n" +
            "WHERE USR.USR_DISPLAY_NAME NOT IN (select LKV_DECODED from LKu \n" +
            "    join lkv on lku.lku_key = lkv.lku_key \n" +
            "    where LKU_TYPE_STRING_KEY = 'Lookup.Actualize.UserVacationExclusion') \n" +
            "AND usr.usr_key IN (SELECT usr_key from usr where act_key IN (select act.act_key\n" +
            "              from act\n" +
            "          where (to_date(sysdate, 'dd.mm.yyyy') between to_date(ORG_UDF_FROMDATE, 'dd.mm.yyyy') \n" +
            "                and to_date(nvl(ORG_UDF_TODATE, sysdate + 1), 'dd.mm.yyyy'))\n" +
            "          start with ORG_UDF_HRORGCODE = :sourceId \n" +
            "          connect by prior ORG_UDF_HRORGCODE = ORG_UDF_HRPARENTORGCODE))";

    private final static String OIM_FIND_USR_TO_ENABLE_BY_SOURCE_IF_EQUAL_QUERY = "SELECT distinct ad.ud_ADUSER_UID AD_LOGIN, 'UPDATE USR SET USR_STATUS = ''Active'' where usr_key = '''||usr.usr_key||'''' enabled_usr,\n" +
            "'UPDATE USER_PROVISIONING_ATTRS SET POLICY_EVAL_IN_PROGRESS = 0, POLICY_EVAL_NEEDED = 1 where usr_key = '''||usr.usr_key||'''' upd_AP\n" +
            "FROM USR\n" +
            "  JOIN ORC on orc.usr_key = usr.usr_key\n" +
            "  join UD_ADUSER ad on ad.ORC_KEY = orc.orc_key\n" +
            "  join oiu on oiu .orc_key = orc.orc_key\n" +
            "  join ost on oiu.ost_key = ost.osT_key and OST_STATUS IN ('Disabled', 'Provisioned', 'Enabled')\n" +
            "WHERE (USR.USR_LOCKED <> '1' OR USR.USR_UDF_EXTENSIONATTRIBUTE15 is null)\n" +
            "AND USR.USR_DISPLAY_NAME NOT IN (select LKV_DECODED from LKu \n" +
            "    join lkv on lku.lku_key = lkv.lku_key \n" +
            "    where LKU_TYPE_STRING_KEY = 'Lookup.Actualize.UserVacationExclusion')\n" +
            "AND (to_date(sysdate, 'dd.mm.yyyy') between to_date(usr.USR_Start_date, 'dd.mm.yyyy') and to_date(nvl(usr.usr_end_date, sysdate + 1), 'dd.mm.yyyy'))\n" +
            "AND (to_date(sysdate, 'dd.mm.yyyy') NOT between to_date(nvl(usr.USR_UDF_STARTDATEVACATION, sysdate -1), 'dd.mm.yyyy') and to_date(nvl(usr.USR_UDF_ENDDATEVACATION, sysdate + 1), 'dd.mm.yyyy') or USR_UDF_STARTDATEVACATION is null) \n" +
            "AND usr.usr_key IN (SELECT usr_key from usr where USR_UDF_OL_0_2 = (SELECT ORG_UDF_HRORGNAME FROM ACT where ORG_UDF_HRORGCODE = :sourceId))";

    private final static String OIM_FIND_USR_TO_ENABLE_BY_SOURCE_IF_NOT_EQUAL_QUERY = "SELECT distinct ad.ud_ADUSER_UID AD_LOGIN, 'UPDATE USR SET USR_STATUS = ''Active'' where usr_key = '''||usr.usr_key||'''' enabled_usr,\n" +
            "'UPDATE USER_PROVISIONING_ATTRS SET POLICY_EVAL_IN_PROGRESS = 0, POLICY_EVAL_NEEDED = 1 where usr_key = '''||usr.usr_key||'''' upd_AP\n" +
            "FROM USR\n" +
            "  JOIN ORC on orc.usr_key = usr.usr_key\n" +
            "  join UD_ADUSER ad on ad.ORC_KEY = orc.orc_key\n" +
            "  join oiu on oiu .orc_key = orc.orc_key\n" +
            "  join ost on oiu.ost_key = ost.osT_key and OST_STATUS IN ('Disabled', 'Provisioned', 'Enabled')\n" +
            "WHERE (USR.USR_LOCKED <> '1' OR USR.USR_UDF_EXTENSIONATTRIBUTE15 is null)\n" +
            "AND USR.USR_DISPLAY_NAME NOT IN (select LKV_DECODED from LKu \n" +
            "    join lkv on lku.lku_key = lkv.lku_key \n" +
            "    where LKU_TYPE_STRING_KEY = 'Lookup.Actualize.UserVacationExclusion')\n" +
            "AND (to_date(sysdate, 'dd.mm.yyyy') between to_date(usr.USR_Start_date, 'dd.mm.yyyy') and to_date(nvl(usr.usr_end_date, sysdate + 1), 'dd.mm.yyyy'))\n" +
            "AND (to_date(sysdate, 'dd.mm.yyyy') NOT between to_date(nvl(usr.USR_UDF_STARTDATEVACATION, sysdate -1), 'dd.mm.yyyy') and to_date(nvl(usr.USR_UDF_ENDDATEVACATION, sysdate + 1), 'dd.mm.yyyy') or USR_UDF_STARTDATEVACATION is null) \n" +
            "AND usr.usr_key IN (SELECT usr_key from usr where act_key IN (select act.act_key\n" +
            "              from act\n" +
            "          where (to_date(sysdate, 'dd.mm.yyyy') between to_date(ORG_UDF_FROMDATE, 'dd.mm.yyyy') \n" +
            "                and to_date(nvl(ORG_UDF_TODATE, sysdate + 1), 'dd.mm.yyyy'))\n" +
            "          start with ORG_UDF_HRORGCODE = :sourceId \n" +
            "          connect by prior ORG_UDF_HRORGCODE = ORG_UDF_HRPARENTORGCODE))";

    private final static String OIM_FIND_USR_TO_BLOCK_BY_EMP_QUERY = "SELECT distinct ad.ud_ADUSER_UID AD_LOGIN, 'UPDATE USR SET USR_STATUS = ''Disabled'' where usr_key = '''||usr.usr_key||'''' disable_usr\n" +
            "FROM USR\n" +
            "  JOIN ORC on orc.usr_key = usr.usr_key\n" +
            "  join UD_ADUSER ad on ad.ORC_KEY = orc.orc_key\n" +
            "  join oiu on oiu .orc_key = orc.orc_key\n" +
            "  join ost on oiu.ost_key = ost.osT_key and OST_STATUS IN ('Disabled', 'Provisioned', 'Enabled')\n" +
            "WHERE  USR.USR_DISPLAY_NAME NOT IN (select LKV_DECODED from LKu \n" +
            "    join lkv on lku.lku_key = lkv.lku_key \n" +
            "    where LKU_TYPE_STRING_KEY = 'Lookup.Actualize.UserVacationExclusion')\n" +
            "AND USR.USR_EMP_NO IN (:empNumbers)";

    private final static String OIM_FIND_USR_TO_ENABLE_BY_EMP_QUERY = "SELECT distinct ad.ud_ADUSER_UID AD_LOGIN, 'UPDATE USR SET USR_STATUS = ''Active'' where usr_key = '''||usr.usr_key||'''' enabled_usr,\n" +
            "'UPDATE USER_PROVISIONING_ATTRS SET POLICY_EVAL_IN_PROGRESS = 0, POLICY_EVAL_NEEDED = 1 where usr_key = '''||usr.usr_key||'''' upd_AP\n" +
            "FROM USR\n" +
            "  JOIN ORC on orc.usr_key = usr.usr_key\n" +
            "  join UD_ADUSER ad on ad.ORC_KEY = orc.orc_key\n" +
            "  join oiu on oiu .orc_key = orc.orc_key\n" +
            "  join ost on oiu.ost_key = ost.osT_key and OST_STATUS IN ('Disabled', 'Provisioned', 'Enabled')\n" +
            "WHERE (USR.USR_LOCKED <> '1' OR USR.USR_UDF_EXTENSIONATTRIBUTE15 is null)\n" +
            "AND USR.USR_DISPLAY_NAME NOT IN (select LKV_DECODED from LKu \n" +
            "    join lkv on lku.lku_key = lkv.lku_key \n" +
            "    where LKU_TYPE_STRING_KEY = 'Lookup.Actualize.UserVacationExclusion')\n" +
            "AND (to_date(sysdate, 'dd.mm.yyyy') between to_date(usr.USR_Start_date, 'dd.mm.yyyy') and to_date(nvl(usr.usr_end_date, sysdate + 1), 'dd.mm.yyyy'))\n" +
            "AND (to_date(sysdate, 'dd.mm.yyyy') NOT between to_date(nvl(usr.USR_UDF_STARTDATEVACATION, sysdate -1), 'dd.mm.yyyy') and to_date(nvl(usr.USR_UDF_ENDDATEVACATION, sysdate + 1), 'dd.mm.yyyy') or USR_UDF_STARTDATEVACATION is null)\n" +
            "AND USR.USR_EMP_NO IN (:empNumbers)";

    private static final String BY_FILE_OIM_USER_QUERY = "SELECT distinct UPPER(ad.ud_ADUSER_UID) AD_LOGIN, 'UPDATE USR SET USR_STATUS = ''Active'' where usr_key = '''||usr.usr_key||'''' enabled_usr,\n" +
            "'UPDATE USR SET USR_STATUS = ''Disabled'' where usr_key = '''||usr.usr_key||'''' disable_usr,\n" +
            "'UPDATE USER_PROVISIONING_ATTRS SET POLICY_EVAL_IN_PROGRESS = 0, POLICY_EVAL_NEEDED = 1 where usr_key = '''||usr.usr_key||'''' upd_AP\n" +
            "FROM USR\n" +
            "  JOIN ORC on orc.usr_key = usr.usr_key\n" +
            "  join UD_ADUSER ad on ad.ORC_KEY = orc.orc_key\n" +
            "  join oiu on oiu .orc_key = orc.orc_key\n" +
            "  join ost on oiu.ost_key = ost.osT_key and OST_STATUS IN ('Disabled', 'Provisioned', 'Enabled')\n" +
            "WHERE UPPER(ad.ud_ADUSER_UID) IN (:adLogins)";

    private static final String CHECK_SOURCE_ID_QUERY = "SELECT ORG_UDF_HRPARENTORGCODE FROM ACT WHERE ORG_UDF_HRORGCODE = :sourceId";

    private static final String OIM_GET_ALL_USERS_WITH_START_REBRANCHING_QUERY =
            "select usr.usr_key USR_KEY, usr.USR_UDF_OBJECTID USR_UDF_OBJECTID, usr.USR_UDF_TEMPBRANCH USR_UDF_TEMPBRANCH, usr.USR_EMP_NO USR_EMP_NO, OMNI_REQUEST.IS_CLOSURE_SENT IS_CLOSURE_SENT " +
                    "from usr, OMNI_REQUEST " +
                    "where usr.USR_EMP_NO = OMNI_REQUEST.EMP_NO " +
                    "and (USR_UDF_CURRENTBRANCH2 <> USR_UDF_TEMPBRANCH or (USR_UDF_CURRENTBRANCH2 is null and USR_UDF_TEMPBRANCH is not null)) " +
                    "and USR_UDF_REBRANCHINGSTARTDATE <=sysdate " +
                    "and USR_UDF_REBRANCHINGENDDATE >= sysdate " +
                    "and OMNI_REQUEST.IS_PROCESSED = 0 and OMNI_REQUEST.IS_SAVED = 1";

    private static final String OIM_GET_ALL_USERS_WITH_END_REBRANCHING_QUERY =
            "select usr.usr_key USR_KEY, usr.USR_UDF_OBJECTID USR_UDF_OBJECTID, usr.USR_UDF_MAINBRANCH USR_UDF_MAINBRANCH, usr.USR_EMP_NO USR_EMP_NO, OMNI_REQUEST.IS_CLOSURE_SENT IS_CLOSURE_SENT " +
                    "from usr, OMNI_REQUEST " +
                    "where usr.USR_EMP_NO = OMNI_REQUEST.EMP_NO " +
                    "and (USR_UDF_CURRENTBRANCH2 <> USR_UDF_MAINBRANCH and USR_UDF_MAINBRANCH is not null or (USR_UDF_CURRENTBRANCH2 is null and USR_UDF_TEMPBRANCH is not null)) " +
                    "and (USR_UDF_REBRANCHINGSTARTDATE > sysdate or USR_UDF_REBRANCHINGENDDATE < sysdate) " +
                    "and OMNI_REQUEST.IS_PROCESSED = 0 and OMNI_REQUEST.IS_SAVED = 1";

    public List<OimUserDto> findUsersForRebranching() {
        return jdbcTemplate.query(OIM_GET_ALL_USERS_WITH_START_REBRANCHING_QUERY, (rs, rowNum) -> OimUserDto.builder()
                .usrKey(rs.getLong("USR_KEY"))
                .tmpBranch(rs.getString("USR_UDF_TEMPBRANCH"))
                .objectId(rs.getString("USR_UDF_OBJECTID"))
                .empNumber(rs.getString("USR_EMP_NO"))
                .isClosureSent(rs.getBoolean("IS_CLOSURE_SENT"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findUsersForBackToMainBranch() {
        return jdbcTemplate.query(OIM_GET_ALL_USERS_WITH_END_REBRANCHING_QUERY, (rs, rowNum) -> OimUserDto.builder()
                .usrKey(rs.getLong("USR_KEY"))
                .mainBranch(rs.getString("USR_UDF_MAINBRANCH"))
                .objectId(rs.getString("USR_UDF_OBJECTID"))
                .empNumber(rs.getString("USR_EMP_NO"))
                .isClosureSent(rs.getBoolean("IS_CLOSURE_SENT"))
                .build());
    ***REMOVED***

    public List<ProcessedUser> findUsersToEnableByAdLogin(List<String> adLogins) {
        List<String> upperCaseLogins = adLogins.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("adLogins", upperCaseLogins);
        return jdbcTemplate.query(BY_FILE_OIM_USER_QUERY, params, (rs, rowNum) -> ProcessedUser.builder()
                .adLogin(rs.getString("AD_LOGIN"))
                .processOimUserScript(rs.getString("enabled_usr"))
                .provisioningScript(rs.getString("upd_AP"))
                .build());
    ***REMOVED***

    public List<ProcessedUser> findUsersToDisableByAdLogin(List<String> adLogins) {
        List<String> upperCaseLogins = adLogins.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("adLogins", upperCaseLogins);
        return jdbcTemplate.query(BY_FILE_OIM_USER_QUERY, params, (rs, rowNum) -> ProcessedUser.builder()
                .adLogin(rs.getString("AD_LOGIN"))
                .processOimUserScript(rs.getString("disable_usr"))
                .build());
    ***REMOVED***


    public void processOimUser(String query) {
        jdbcTemplate.execute(query, PreparedStatement::executeUpdate);
    ***REMOVED***

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

    public List<ProcessedUser> findUsersToBlockByEmployeeNumber(List<String> empNumbers) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("empNumbers", empNumbers);
        return jdbcTemplate.query(OIM_FIND_USR_TO_BLOCK_BY_EMP_QUERY, params, (rs, rowNum) -> ProcessedUser.builder()
                .adLogin(rs.getString("AD_LOGIN"))
                .processOimUserScript(rs.getString("disable_usr"))
                .build());
    ***REMOVED***

    public List<ProcessedUser> findUsersToEnableByEmployeeNumber(List<String> empNumbers) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("empNumbers", empNumbers);
        return jdbcTemplate.query(OIM_FIND_USR_TO_ENABLE_BY_EMP_QUERY, params, (rs, rowNum) -> ProcessedUser.builder()
                .adLogin(rs.getString("AD_LOGIN"))
                .processOimUserScript(rs.getString("enabled_usr"))
                .provisioningScript(rs.getString("upd_AP"))
                .build());
    ***REMOVED***

    public List<ProcessedUser> findUsersToBlockBySourceIdIfEqual(String sourceId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("sourceId", sourceId);
        return jdbcTemplate.query(OIM_FIND_USR_TO_BLOCK_BY_SOURCE_IF_EQUAL_QUERY, params, (rs, rowNum) -> ProcessedUser.builder()
                .adLogin(rs.getString("AD_LOGIN"))
                .processOimUserScript(rs.getString("disable_usr"))
                .build());
    ***REMOVED***

    public List<ProcessedUser> findUsersToBlockBySourceIdIfNotEqual(String sourceId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("sourceId", sourceId);
        return jdbcTemplate.query(OIM_FIND_USR_TO_BLOCK_BY_SOURCE_IF_NOT_EQUAL_QUERY, params, (rs, rowNum) -> ProcessedUser.builder()
                .adLogin(rs.getString("AD_LOGIN"))
                .processOimUserScript(rs.getString("disable_usr"))
                .build());
    ***REMOVED***

    public List<ProcessedUser> findUsersToEnableBySourceIdIfEqual(String sourceId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("sourceId", sourceId);
        return jdbcTemplate.query(OIM_FIND_USR_TO_ENABLE_BY_SOURCE_IF_EQUAL_QUERY, params, (rs, rowNum) -> ProcessedUser.builder()
                .adLogin(rs.getString("AD_LOGIN"))
                .processOimUserScript(rs.getString("enabled_usr"))
                .provisioningScript(rs.getString("upd_AP"))
                .build());
    ***REMOVED***

    public List<ProcessedUser> findUsersToEnableBySourceIdIfNotEqual(String sourceId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("sourceId", sourceId);
        return jdbcTemplate.query(OIM_FIND_USR_TO_ENABLE_BY_SOURCE_IF_NOT_EQUAL_QUERY, params, (rs, rowNum) -> ProcessedUser.builder()
                .adLogin(rs.getString("AD_LOGIN"))
                .processOimUserScript(rs.getString("enabled_usr"))
                .provisioningScript(rs.getString("upd_AP"))
                .build());
    ***REMOVED***

    public String checkSourceId(String sourceId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("sourceId", sourceId);

        List<String> query = jdbcTemplate.query(CHECK_SOURCE_ID_QUERY, params, (rs, rowNum) -> rs.getString(1));
        return query.get(0);
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
                .action(rs.getString("ACTION"))
                .actionDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("ACTION_DATE")))
                .isPickupSent(rs.getBoolean("IS_PICKUP_SENT"))
                .isClosureSent(rs.getBoolean("IS_CLOSURE_SENT"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findAllUnprocessedAttachmentsRequests() {
        return jdbcTemplate.query(OMNI_FIND_ALL_BLOCK_ATTACHMENT_QUERY, (rs, rowNum) -> OimUserDto.builder()
                .objectId(rs.getString("OBJECT_ID"))
                .action(rs.getString("ACTION"))
                .actionDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("ACTION_DATE")))
                .isPickupSent(rs.getBoolean("IS_PICKUP_SENT"))
                .isClosureSent(rs.getBoolean("IS_CLOSURE_SENT"))
                .id(rs.getLong("ID"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findUsersToProcess(LocalDateTime date, String objectId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("actionDate", date.truncatedTo(MINUTES))
                .addValue("objectId", objectId);
        return jdbcTemplate.query(OMNI_FIND_USER_TO_BLOCK_QUERY, namedParameters, (rs, rowNum) -> OimUserDto.builder()
                .empNumber(rs.getString("EMP_NUMBER"))
                .objectId(rs.getString("OBJECT_ID"))
                .action(rs.getString("ACTION"))
                .sourceId(rs.getString("SOURCE_ID"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findAttachment(LocalDateTime date, String objectId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("actionDate", date.truncatedTo(MINUTES))
                .addValue("objectId", objectId);
        return jdbcTemplate.query(OMNI_FIND_USER_TO_BLOCK_ATTACHMENT_QUERY, namedParameters, (rs, rowNum) -> OimUserDto.builder()
                .attachment(rs.getString("ATTACHMENT"))
                .objectId(rs.getString("OBJECT_ID"))
                .action(rs.getString("ACTION"))
                .build());
    ***REMOVED***

    public List<OimUserDto> findAttachmentToSave(String objectId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("objectId", objectId);
        return jdbcTemplate.query(OMNI_FIND_ATTACHMENT_QUERY, namedParameters, (rs, rowNum) -> OimUserDto.builder()
                .attachment(rs.getString("ATTACHMENT"))
                .oid(rs.getLong("OID"))
                .objectId(rs.getString("OBJECT_ID"))
                .id(rs.getLong("attachment_id"))
                .build());
    ***REMOVED***

    public List<Long> findOimUserByEmpNumber(String empNumber) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("empNumber", empNumber);

        return jdbcTemplate.query(OIM_FIND_USER_BY_EMP_NUMBER_QUERY, namedParameters, (rs, rowNum) ->
                rs.getLong("USR_KEY"));
    ***REMOVED***

    public Integer updateOimUser(OimUserDto oimUserDto) {
        Date endDate = Date.valueOf(Date.valueOf(oimUserDto.getEndDate()).toLocalDate().plusDays(1));
        SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                .addValue("empNumber", oimUserDto.getEmpNumber())
                .addValue("objectId", oimUserDto.getObjectId())
                .addValue("mainBranch", oimUserDto.getMainBranch())
                .addValue("tmpBranch", oimUserDto.getTmpBranch())
                .addValue("startDate", java.sql.Date.valueOf(oimUserDto.getStartDate()))
                .addValue("endDate", endDate);
        return jdbcTemplate.execute(OIM_UPDATE_USER_BY_EMP_NUMBER_QUERY, namedParametersForUpdate, PreparedStatement::executeUpdate);
    ***REMOVED***

    public Integer updateAttachments(Long id, String attachment) {
        SqlParameterSource namedParametersForUpdate = new MapSqlParameterSource()
                .addValue("id", id)
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
