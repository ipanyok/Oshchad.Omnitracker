package ua.datastech.omnitracker.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;
import ua.datastech.omnitracker.service.tracker.api.OmnitrackerApiService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerJob {

    private final OmnitrackerApiService omnitrackerApiService;
    private final JdbcQueryService jdbcQueryService;

    @Async("CustomAsyncOmniExecutor")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void sendOmniDataPickup() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedRequests(false);
        LocalDate currentDate = LocalDate.now();
        omniData.forEach(oimUserDto -> {
            try {
                if (checkIfDateExpired(oimUserDto, currentDate)) {
                    return;
                }

                if (!oimUserDto.getIsPickupSent()) {
                    omnitrackerApiService.callOmniTrackerPickupService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId());
                }

            } catch (Exception e) {
                log.error("Can't process " + oimUserDto.getObjectId() + " request", e);
            }
        });
    }

    @Async("CustomAsyncOmniExecutor")
    @Scheduled(cron = "0 0/30 * * * ?")
    public void processRebranching() {
        saveDataToOimUser();
        closeRequestsWithCurrentBranchEqualsTempBranch();
        rebranching();
        backBranch();
    }

    @Async("CustomAsyncOmniExecutor")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void closeRequests() {
        List<OimUserDto> requestsToClose = jdbcQueryService.getRebranchRequestObjectIdsToClose();
        requestsToClose.forEach(oimUserDto -> {
            try {
                if (!oimUserDto.getIsPickupSent()) {
                    omnitrackerApiService.callOmniTrackerPickupService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId());
                }
                omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_CANCELLED, "Відхилено. Обробка звернення завершена за ініціативою Банка.", "");
                jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            } catch (Exception e) {
                log.error("Can't close " + oimUserDto.getObjectId() + " request", e);
            }
        });
    }

    private void rebranching() {
        List<OimUserDto> rebranchedUsers = jdbcQueryService.findUsersForRebranching();
        rebranchedUsers.forEach(oimUserDto -> {
            if (!oimUserDto.getIsClosureSent()) {
                log.info("Actualize user with key: " + oimUserDto.getEmpNumber() + ". Current (temp) branch is: " + oimUserDto.getTmpBranch());
                try {
                    jdbcQueryService.updateOimUsrForRebranch(oimUserDto.getTmpBranch(), oimUserDto.getUsrKey());
                    omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, "Вирішено", "");
                } catch (Exception e) {
                    log.error("Can't modify user with key: " + oimUserDto.getEmpNumber(), e);
                    omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Не можливо оновити користувача " + oimUserDto.getEmpNumber(), "");
                }
            }
            jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
        });
    }

    private void backBranch() {
        List<OimUserDto> rebranchedUsers = jdbcQueryService.findUsersForBackToMainBranch();
        rebranchedUsers.forEach(oimUserDto -> {
            log.info("Actualize user with key: " + oimUserDto.getEmpNumber() + ". Current (main) branch is: " + oimUserDto.getMainBranch());
            try {
                jdbcQueryService.updateOimUsrForRebranch(oimUserDto.getMainBranch(), oimUserDto.getUsrKey());
            } catch (Exception e) {
                log.error("Can't modify user with key: " + oimUserDto.getEmpNumber(), e);
            }
        });
    }

    private void closeRequestsWithCurrentBranchEqualsTempBranch() {
        List<OimUserDto> usersWithBranches = jdbcQueryService.getUsersBranches();
        List<OimUserDto> nonProcessedUsers = usersWithBranches.stream()
                .filter(oimUserDto -> oimUserDto.getTmpBranch().equals(oimUserDto.getCurrentBranch()))
                .collect(Collectors.toList());
        nonProcessedUsers.forEach(oimUserDto -> {
            if (!oimUserDto.getIsClosureSent()) {
                log.info("User with key: " + oimUserDto.getEmpNumber() + " already into the temp branch. Current (temp) branch is: " + oimUserDto.getTmpBranch());
                try {
                    omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, "Вирішено. Користувач вже знаходиться в даному ТВБВ", "");
                    jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
                } catch (Exception e) {
                    log.error("Can't sent closure for user with key: " + oimUserDto.getEmpNumber(), e);
                }
            }
        });
    }

    private boolean checkIfDateExpired(OimUserDto oimUserDto, LocalDate currentDate) {
        boolean isExpired = false;
        if (LocalDate.parse(oimUserDto.getStartDate()).isBefore(currentDate) && !oimUserDto.getIsClosureSent()) {
            if (!oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerPickupService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId());
            }
            omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Дата старту менша поточної", "");
            jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            isExpired = true;
        }
        return isExpired;
    }

    private void saveDataToOimUser() {
        List<OimUserDto> oimUserDtos = filterOnlyLatestRequests(jdbcQueryService.findAllUnprocessedRequests(true));
        updateOimUser(oimUserDtos);
    }

    private List<OimUserDto> filterOnlyLatestRequests(List<OimUserDto> omniData) {
        List<List<OimUserDto>> sameElementsList = omniData.stream()
                .collect(groupingBy(OimUserDto::getEmpNumber))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(entry -> entry.getValue())
                .collect(toList());

        List<OimUserDto> toLeaveList = new ArrayList<>();
        for (List<OimUserDto> oimUserDtos : sameElementsList) {
            toLeaveList.add(oimUserDtos.stream()
                    .max(Comparator.comparing(OimUserDto::getChangedAt)).get());
        }
        omniData.removeIf(oimUserDto ->
                toLeaveList.stream()
                        .filter(o -> o.getEmpNumber().equals(oimUserDto.getEmpNumber()) && !o.getChangedAt().equals(oimUserDto.getChangedAt()))
                        .findFirst()
                        .orElse(null) != null
        );
        return omniData;
    }

    private void updateOimUser(List<OimUserDto> oimUserDtos) {
        oimUserDtos.forEach(oimUserDto -> {
            List<Long> ids = jdbcQueryService.findOimUserByEmpNumber(oimUserDto.getEmpNumber());

            if (ids.isEmpty()) {
                log.info("User [empNumber=" + oimUserDto.getEmpNumber() + "] wasn't found.");

                jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));

                if (!oimUserDto.getIsClosureSent()) {
                    omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Користувач [empNumber=" + oimUserDto.getEmpNumber() + "] не знайдений в системі ОІМ.", "");
                }
            } else {
                Integer updateCount = jdbcQueryService.updateOimUser(oimUserDto);
                if (updateCount != 0) {
                    jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_SAVED", "1"));
                    log.info("User[empNumber=" + oimUserDto.getEmpNumber() + "] data was saved in OIM");
                }
            }
        });
    }

}
