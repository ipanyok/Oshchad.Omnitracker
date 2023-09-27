package ua.datastech.omnitracker.service.tracker.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ua.datastech.omnitracker.model.oim.OmniTrackerRequest;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static ua.datastech.omnitracker.model.dto.ActionType.RE_BRANCH;

@Component
@Slf4j
@RequiredArgsConstructor
public class RebranchProcessor implements OmniRequestProcessor {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void process(OmniTrackerRequest request) {
        java.sql.Date endDate = null;
        if (request.getAdditionalInfo().getEndDate() != null && !request.getAdditionalInfo().getEndDate().equals("")) {
            endDate = java.sql.Date.valueOf(request.getAdditionalInfo().getEndDate().substring(0, request.getAdditionalInfo().getEndDate().indexOf("T")));
        }
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("objectId", request.getObjectID())
                .addValue("empNumber", request.getAdditionalInfo().getEmpNumber())
                .addValue("mainBranch", request.getAdditionalInfo().getMainBranch())
                .addValue("tmpBranch", request.getAdditionalInfo().getTmpBranch())
                .addValue("startDate", java.sql.Date.valueOf(request.getAdditionalInfo().getStartDate().substring(0, request.getAdditionalInfo().getStartDate().indexOf("T"))))
                .addValue("endDate", endDate)
                .addValue("localDate", LocalDateTime.now());
        Integer execute = jdbcTemplate.execute("insert into omni_request (OBJECT_ID, EMP_NO, MAINBRANCH, TEMPBRANCH, REBRANCHINGSTARTDATE, REBRANCHINGENDDATE, CHANGED_AT) VALUES (:objectId, :empNumber, :mainBranch, :tmpBranch, :startDate, :endDate, :localDate)",
                namedParameters,
                PreparedStatement::executeUpdate
        );
        if (execute != 0) {
            log.info("Omni request " + request.getObjectID() + " was saved.");
        }
    }

    @Override
    public Set<String> getActions() {
        Set<String> actionTypes = new HashSet<>();
        actionTypes.add(RE_BRANCH.name());
        return actionTypes;
    }
}
