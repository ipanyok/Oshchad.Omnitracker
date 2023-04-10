package ua.datastech.omnitracker.service.tracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.ActionType;
import ua.datastech.omnitracker.model.oim.OmniTrackerAttachmentInfoRequest;
import ua.datastech.omnitracker.model.oim.OmniTrackerRequest;
import ua.datastech.omnitracker.service.tracker.processor.OmniRequestProcessor;

import java.sql.PreparedStatement;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerService {

    @Value("${spring.profiles.active***REMOVED***")
    private String profile;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final List<OmniRequestProcessor> omniRequestProcessors;

    public void saveOmniBlockRequest(OmniTrackerRequest request) {
        OmniRequestProcessor processor = getProcessor(request);
        processor.process(request);
    ***REMOVED***

    public void saveOmniAttachmentRequest(OmniTrackerAttachmentInfoRequest request) {
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

    private OmniRequestProcessor getProcessor(OmniTrackerRequest request) {
        String actionName;
        if (profile.equals("prod")) {
            actionName = ActionType.findActionByServiceTypeIdProd(request.getServiceTypeID());
        ***REMOVED*** else {
            actionName = ActionType.findActionByServiceTypeIdTest(request.getServiceTypeID());
        ***REMOVED***
        request.setServiceTypeID(actionName);
        return omniRequestProcessors.stream()
                .filter(omniRequestProcessor -> omniRequestProcessor.getActions().contains(actionName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find any processors"));
    ***REMOVED***

***REMOVED***
