package ua.datastech.omnitracker.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ActionType {
    RE_BRANCH("127064969"),
    DISABLE_USER("127609143"),
    ENABLE_USER("127791269"),
    DISABLE_REGION("127609144"),
    ENABLE_REGION("127791270"),
    DISABLE_BY_FILE("127768030"),
    ENABLE_BY_FILE("127791271");

    private final String serviceTypeId;

    public static String findActionByServiceTypeId(String serviceTypeId) {
        return Arrays.stream(ActionType.values())
                .filter(actionType -> actionType.getServiceTypeId().equals(serviceTypeId))
                .map(actionType -> actionType.name())
                .findFirst()
                .orElse(null);
    ***REMOVED***

***REMOVED***
