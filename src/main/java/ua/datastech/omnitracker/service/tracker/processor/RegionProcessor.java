package ua.datastech.omnitracker.service.tracker.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ua.datastech.omnitracker.model.oim.OmniTrackerRequest;

import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.datastech.omnitracker.model.dto.ActionType.DISABLE_REGION;
import static ua.datastech.omnitracker.model.dto.ActionType.ENABLE_REGION;


@Component
@Slf4j
@RequiredArgsConstructor
public class RegionProcessor implements OmniRequestProcessor {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void process(OmniTrackerRequest request) {
        Integer execute = saveOmniBlockRequest(request, jdbcTemplate);
        request.getAdditionalInfo().getOrganizations().forEach(org -> {
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("objectId", request.getObjectID())
                    .addValue("sourceId", org.getSourceID());
            List<String> ids = jdbcTemplate.query("SELECT ID FROM OMNI_BLOCK_REQUEST WHERE OBJECT_ID = :objectId", params, (rs, rowNum) -> rs.getString("ID"));
            jdbcTemplate.execute("insert into OMNI_BLOCK_DATA (OMNI_BLOCK_REQUEST_ID, SOURCE_ID) VALUES (" + ids.get(0) + ", :sourceId)",
                    params,
                    PreparedStatement::executeUpdate
            );
        });
        if (execute != 0) {
            log.info("Omni block request " + request.getObjectID() + " was saved.");
        }
    }

    @Override
    public Set<String> getActions() {
        Set<String> actionTypes = new HashSet<>();
        actionTypes.add(DISABLE_REGION.name());
        actionTypes.add(ENABLE_REGION.name());
        return actionTypes;
    }
}
