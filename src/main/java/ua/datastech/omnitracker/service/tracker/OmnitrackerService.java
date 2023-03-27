package ua.datastech.omnitracker.service.tracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.ActionType;
import ua.datastech.omnitracker.model.oim.OmniTrackerAttachmentInfoRequest;
import ua.datastech.omnitracker.model.oim.OmniTrackerRequest;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;

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

    public void saveOmniBlockRequest(OmniTrackerRequest request) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("objectId", request.getObjectID())
                .addValue("action", parseServiceTypeId(request.getServiceTypeID()))
                .addValue("actionDate", java.sql.Date.valueOf(request.getAdditionalInfo().getDate().substring(0, request.getAdditionalInfo().getDate().indexOf("T"))))
                .addValue("localDate", LocalDateTime.now());
        Integer execute = jdbcTemplate.execute("insert into OMNI_BLOCK_REQUEST (OBJECT_ID, ACTION, ACTION_DATE, CHANGED_AT) VALUES (:objectId, :action, :actionDate, :localDate)",
                namedParameters,
                PreparedStatement::executeUpdate
        );
        if (request.getAdditionalInfo().getPersons() != null) {
            request.getAdditionalInfo().getPersons().forEach(person -> {
                SqlParameterSource params = new MapSqlParameterSource()
                        .addValue("objectId", request.getObjectID())
                        .addValue("empNumber", person.getEmployeeID());
                List<String> ids = jdbcTemplate.query("SELECT ID FROM OMNI_BLOCK_REQUEST WHERE OBJECT_ID = :objectId", params, (rs, rowNum) -> rs.getString("ID"));
                jdbcTemplate.execute("insert into OMNI_BLOCK_DATA (OMNI_BLOCK_REQUEST_ID, EMP_NUMBER) VALUES (" + ids.get(0) + ", :empNumber)",
                        params,
                        PreparedStatement::executeUpdate
                );
            ***REMOVED***);
        ***REMOVED***
        if (execute != 0) {
            log.info("Omni block request " + request.getObjectID() + " was saved.");
        ***REMOVED***
    ***REMOVED***

    public void saveOmniAttachmetRequest(OmniTrackerAttachmentInfoRequest request) {
        request.getAttachments().forEach(attachment -> {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("objectId", request.getObjectID())
                    .addValue("oid", attachment.getOid())
                    .addValue("fileName", attachment.getFileName())
                    .addValue("fileSize", attachment.getFileSize());
            List<String> ids = jdbcTemplate.query("SELECT ID FROM OMNI_BLOCK_REQUEST WHERE OBJECT_ID = :objectId", namedParameters, (rs, rowNum) -> rs.getString("ID"));
            Integer execute = jdbcTemplate.execute("insert into OMNI_BLOCK_ATTACHMENT (OMNI_BLOCK_REQUEST_ID, OID, FILENAME, FILESIZE) VALUES (" + ids.get(0) + ", :oid, :fileName, :fileSize)",
                    namedParameters,
                    PreparedStatement::executeUpdate
            );
            if (execute != 0) {
                log.info("Attachment data for request " + request.getObjectID() + " was saved.");
            ***REMOVED***
        ***REMOVED***);
    ***REMOVED***

    private String parseServiceTypeId(String serviceTypeId) {
        String result;
        switch (serviceTypeId) {
            case "127064969": {
                result = ActionType.RE_BRANCH.name();
                break;
            ***REMOVED***
            case "127609143": {
                result = ActionType.DISABLE_USER.name();
                break;
            ***REMOVED***
            case "127609144": {
                result = ActionType.DISABLE_REGION.name();
                break;
            ***REMOVED***
            case "127768030": {
                result = ActionType.DISABLE_BY_FILE.name();
                break;
            ***REMOVED***
            case "127791269": {
                result = ActionType.ENABLE_USER.name();
                break;
            ***REMOVED***
            case "127791270": {
                result = ActionType.ENABLE_REGION.name();
                break;
            ***REMOVED***
            case "127791271": {
                result = ActionType.ENABLE_BY_FILE.name();
                break;
            ***REMOVED***
            default: result = null;
        ***REMOVED***
        return result;
    ***REMOVED***

***REMOVED***
