package ua.datastech.omnitracker.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ActionType {
    RE_BRANCH("127064969", "136912548"),
    DISABLE_USER("127609143", "136911617"),
    ENABLE_USER("127791269", "136911762"),
    DISABLE_REGION("127609144", "136912166"),
    ENABLE_REGION("127791270", "136912225"),
    DISABLE_BY_FILE("127768030", "136912286"),
    ENABLE_BY_FILE("127791271", "136912525");

    private final String serviceTypeIdTest;
    private final String serviceTypeIdProd;

    public static String findActionByServiceTypeIdTest(String serviceTypeId) {
        return Arrays.stream(ActionType.values())
                .filter(actionType -> actionType.getServiceTypeIdTest().equals(serviceTypeId))
                .map(actionType -> actionType.name())
                .findFirst()
                .orElse(null);
    ***REMOVED***

    public static String findActionByServiceTypeIdProd(String serviceTypeId) {
        return Arrays.stream(ActionType.values())
                .filter(actionType -> actionType.getServiceTypeIdProd().equals(serviceTypeId))
                .map(actionType -> actionType.name())
                .findFirst()
                .orElse(null);
    ***REMOVED***

***REMOVED***
