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
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;
import ua.datastech.omnitracker.service.tracker.api.OmnitrackerApiService;
import ua.datastech.omnitracker.service.tracker.processor.OmniRequestProcessor;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerService {

    @Value("${spring.profiles.active}")
    private String profile;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OmnitrackerApiService omnitrackerApiService;
    private final JdbcQueryService jdbcQueryService;

    private final List<OmniRequestProcessor> omniRequestProcessors;

    public void saveOmniBlockRequest(OmniTrackerRequest request) {
        OmniRequestProcessor processor = getProcessor(request);
        processor.process(request);
    }

    public void saveOmniAttachmentRequest(OmniTrackerAttachmentInfoRequest request) {
        if (request.getAdditionalInfo() != null && request.getAdditionalInfo().getIsClosed() != null && request.getAdditionalInfo().getIsClosed()) {
            jdbcQueryService.updateBlockRequest(request.getObjectID());
            jdbcQueryService.updateRebranchRequest(request.getObjectID()); // todo probably don't need 2 queries (use some param from request)
            return;
        }
        String action = jdbcQueryService.getAttachmentAction(request.getObjectID());
        if (action != null && (action.equals(ActionType.ENABLE_BY_FILE.name()) || action.equals(ActionType.DISABLE_BY_FILE.name()))) {
            if (request != null && request.getAttachments() != null) {
                try {
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
                        }
                    });
                } catch (Exception e) {
                    log.error("Can't save attachment for objectId: " + request.getObjectID(), e.getMessage());
                    closeRequest(request.getObjectID(), "Помилка під час збереження вкладення.");
                }
            }
        }
    }

    private void closeRequest(String objectId, String reason) {
        omnitrackerApiService.callOmniTrackerPickupService(null, objectId);
        omnitrackerApiService.callOmniTrackerClosureService(null, objectId, ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. " + reason, "");
        jdbcQueryService.updateOmniBlockRequestQuery(objectId, Collections.singletonMap("IS_PROCESSED", "1"));
    }

    private OmniRequestProcessor getProcessor(OmniTrackerRequest request) {
        String actionName;
        if (profile.equals("prod")) {
            actionName = ActionType.findActionByServiceTypeIdProd(request.getServiceTypeID());
        } else {
            actionName = ActionType.findActionByServiceTypeIdTest(request.getServiceTypeID());
        }
        request.setServiceTypeID(actionName); // todo very bad solution!
        return omniRequestProcessors.stream()
                .filter(omniRequestProcessor -> omniRequestProcessor.getActions().contains(actionName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find any processors"));
    }

}
