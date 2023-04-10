package ua.datastech.omnitracker.service.tracker.processor;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import ua.datastech.omnitracker.model.oim.OmniTrackerRequest;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public interface OmniRequestProcessor {

    void process(OmniTrackerRequest request);
    Set<String> getActions();

    default Integer saveOmniBlockRequest(OmniTrackerRequest request, NamedParameterJdbcTemplate jdbcTemplate) {
        LocalDateTime parseTime = LocalDateTime.parse(request.getAdditionalInfo().getDate().substring(0, 20));
        if (parseTime.getMinute() != 0) {
            int minutes = 10 - parseTime.getMinute();
            while (minutes < 0) {
                minutes = minutes + 10;
            ***REMOVED***
            parseTime = parseTime.plusMinutes(minutes).withSecond(0);
        ***REMOVED***
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("objectId", request.getObjectID())
                .addValue("action", request.getServiceTypeID())
                .addValue("actionDate", java.sql.Timestamp.valueOf(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(parseTime)))
                .addValue("localDate", LocalDateTime.now());
        return jdbcTemplate.execute("insert into OMNI_BLOCK_REQUEST (OBJECT_ID, ACTION, ACTION_DATE, CHANGED_AT) VALUES (:objectId, :action, :actionDate, :localDate)",
                namedParameters,
                PreparedStatement::executeUpdate
        );
    ***REMOVED***

***REMOVED***
