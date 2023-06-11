package ua.datastech.omnitracker.service.tracker.close;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import ua.datastech.omnitracker.model.dto.OimUserDto;

import java.sql.PreparedStatement;
import java.util.Set;

public interface CloseRequest {

    void cleanup(OimUserDto oimUserDto);
    Set<String> getActions();

    default Integer updateMainRequest(Long omniBlockRequestId, NamedParameterJdbcTemplate jdbcTemplate) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", omniBlockRequestId);
        return jdbcTemplate.execute("update OMNI_BLOCK_REQUEST set IS_PROCESSED = 1 where ID = :id",
                namedParameters,
                PreparedStatement::executeUpdate
        );
    ***REMOVED***

***REMOVED***
