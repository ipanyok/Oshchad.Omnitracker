package ua.datastech.omnitracker.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.ActionType;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.oim.ProcessedUser;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;
import ua.datastech.omnitracker.service.parse.ExcelFileReader;
import ua.datastech.omnitracker.service.script.PowerShellExecutor;
import ua.datastech.omnitracker.service.tracker.api.OmnitrackerApiService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerBlockJob {

    private final OmnitrackerApiService omnitrackerApiService;
    private final JdbcQueryService jdbcQueryService;
    private final PowerShellExecutor powerShellExecutor;

    // todo think about transactions and try/catch sections (when send closure)
//    @Transactional
    @Async("CustomAsyncOmniExecutor")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void processData() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedBlockRequests();
        LocalDateTime currentDate = LocalDateTime.now();
        omniData.forEach(oimUserDto -> {
            try {
                if (checkIfDateExpired(oimUserDto, currentDate)) {
                    return;
                }

                if (!oimUserDto.getIsPickupSent()) {
                    omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
                }

                List<OimUserDto> usersDataToBlock = jdbcQueryService.findUsersToProcess(currentDate, oimUserDto.getObjectId());
                List<ProcessedUser> users = null;
                if (oimUserDto.getAction().equals(ActionType.DISABLE_USER.name())) {
                    List<String> empNumbers = usersDataToBlock.stream().map(OimUserDto::getEmpNumber).collect(Collectors.toList());
                    if (empNumbers != null && !empNumbers.isEmpty()) {
                        users = jdbcQueryService.findUsersToBlockByEmployeeNumber(empNumbers);
                    }
                }
                if (oimUserDto.getAction().equals(ActionType.ENABLE_USER.name())) {
                    List<String> empNumbers = usersDataToBlock.stream().map(OimUserDto::getEmpNumber).collect(Collectors.toList());
                    if (empNumbers != null && !empNumbers.isEmpty()) {
                        users = jdbcQueryService.findUsersToEnableByEmployeeNumber(empNumbers);
                    }
                }
                if (oimUserDto.getAction().equals(ActionType.DISABLE_REGION.name())) {
                    List<String> sourceIds = usersDataToBlock.stream().map(OimUserDto::getSourceId).collect(Collectors.toList());
                    users = new ArrayList<>();
                    for (String sourceId : sourceIds) {
                        String parentOrgCode = jdbcQueryService.checkSourceId(sourceId);
                        if (parentOrgCode.equals(sourceId)) {
                            users.addAll(jdbcQueryService.findUsersToBlockBySourceIdIfEqual(sourceId));
                        } else {
                            users.addAll(jdbcQueryService.findUsersToBlockBySourceIdIfNotEqual(sourceId));
                        }
                    }
                }
                if (oimUserDto.getAction().equals(ActionType.ENABLE_REGION.name())) {
                    List<String> sourceIds = usersDataToBlock.stream().map(OimUserDto::getSourceId).collect(Collectors.toList());
                    users = new ArrayList<>();
                    for (String sourceId : sourceIds) {
                        String parentOrgCode = jdbcQueryService.checkSourceId(sourceId);
                        if (parentOrgCode.equals(sourceId)) {
                            users.addAll(jdbcQueryService.findUsersToEnableBySourceIdIfEqual(sourceId));
                        } else {
                            users.addAll(jdbcQueryService.findUsersToEnableBySourceIdIfNotEqual(sourceId));
                        }
                    }
                }

                if ((usersDataToBlock != null && !usersDataToBlock.isEmpty()) && (users == null || users.isEmpty())) {
                    omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Не знайдено жодного користувача в AD.", "");
                    jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
                }
                if (users != null && !users.isEmpty()) {
                    List<String> unprocessedUsers = powerShellExecutor.execute(oimUserDto.getAction(), users);
                    String result;
                    if (unprocessedUsers.isEmpty()) {
                        result = "Вирішено";
                    } else {
                        result = "Вирішено. \nНе оброблені користувачі: \n" + unprocessedUsers;
                    }
                    omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, result, "");
                    jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
                }
            } catch (Exception e) {
                log.error("Can't process " + oimUserDto.getObjectId() + " request: " + e.getMessage());
            }
        });
    }

    @Async("CustomAsyncOmniExecutor")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void processAttachmentsData() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedAttachmentsRequests();
        LocalDateTime currentDate = LocalDateTime.now();
        omniData.forEach(oimUserDto -> {
            try {
                if (checkIfDateExpired(oimUserDto, currentDate)) {
                    return;
                }

                List<OimUserDto> attachmentsToSave = jdbcQueryService.findAttachmentToSave(oimUserDto.getObjectId());
                if (attachmentsToSave != null && !attachmentsToSave.isEmpty()) {
                    if (!oimUserDto.getIsPickupSent()) {
                        attachmentsToSave.forEach(attachment -> {
                            String attachmentString = omnitrackerApiService.callOmniTrackerGetAttachmentService(attachment.getOid(), attachment.getObjectId());
                            Integer execute = jdbcQueryService.updateAttachments(attachment.getId(), attachmentString);
                            if (execute != 0) {
                                log.info("Attachment for request " + oimUserDto.getObjectId() + " was decoded.");
                            }
                        });
                        omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
                        oimUserDto.setIsPickupSent(true);
                    }
                }

                List<OimUserDto> attachmentData = jdbcQueryService.findAttachment(currentDate, oimUserDto.getObjectId());

                AtomicBoolean isProcessed = new AtomicBoolean(false);
                AtomicReference<List<String>> unprocessedUsers = new AtomicReference<>();
                attachmentData.forEach(data -> {
                    List<String> users = ExcelFileReader.read(data.getAttachment());
                    List<ProcessedUser> processedUsers;
                    if (users != null && !users.isEmpty()) {
                        List<String> trimUsers = users.stream().map(String::trim).collect(Collectors.toList());
                        if (data.getAction().equals(ActionType.ENABLE_BY_FILE.name())) {
                            processedUsers = jdbcQueryService.findUsersToEnableByAdLogin(trimUsers);
                        } else {
                            processedUsers = jdbcQueryService.findUsersToDisableByAdLogin(trimUsers);
                        }

                        List<String> usersNonProcessed = trimUsers.stream()
                                .map(String::toUpperCase)
                                .filter(user -> !isContainsInProcessedUsers(processedUsers, user))
                                .collect(Collectors.toList());

                        List<String> existingUnprocessedUsers = unprocessedUsers.get();
                        List<String> scriptUnprocessedUsers = powerShellExecutor.execute(data.getAction(), processedUsers);
                        if (!usersNonProcessed.isEmpty()) {
                            scriptUnprocessedUsers.addAll(usersNonProcessed);
                        }
                        if (existingUnprocessedUsers == null) {
                            unprocessedUsers.set(scriptUnprocessedUsers);
                        } else {
                            existingUnprocessedUsers.addAll(scriptUnprocessedUsers);
                            unprocessedUsers.set(existingUnprocessedUsers);
                        }
                        isProcessed.set(true);
                    }
                });
                if (isProcessed.get()) {
                    String result;
                    if (unprocessedUsers.get().isEmpty()) {
                        result = "Вирішено";
                    } else {
                        result = "Вирішено. \nНе оброблені користувачі: \n" + unprocessedUsers;
                    }
                    omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, result, "");
                    jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
                } else if ((attachmentData != null && !attachmentData.isEmpty()) && oimUserDto.getIsPickupSent()) {
                    omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Не знайдено жодного користувача в AD або вкладення не містить логінів.", "");
                    jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
                }
            } catch (Exception e) {
                log.error("Can't process " + oimUserDto.getObjectId() + " request: " + e.getMessage());
            }
        });
    }

    @Async("CustomAsyncOmniExecutor")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void closeRequests() {
        List<OimUserDto> requestsToClose = jdbcQueryService.getBlockRequestObjectIdsToClose();
        requestsToClose.forEach(oimUserDto -> {
            try {
                if (!oimUserDto.getIsPickupSent()) {
                    omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
                }
                omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_CANCELLED, "Відхилено. Обробка звернення завершена за ініціативою Банка.", "");
                jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            } catch (Exception e) {
                log.error("Can't close " + oimUserDto.getObjectId() + " request", e);
            }
        });
    }

    private boolean checkIfDateExpired(OimUserDto oimUserDto, LocalDateTime currentDate) {
        boolean isExpired = false;
        if (LocalDateTime.parse(oimUserDto.getActionDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).truncatedTo(MINUTES).isBefore(currentDate.truncatedTo(MINUTES)) && !oimUserDto.getIsClosureSent()) {
            if (!oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
            }
            omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Дата блокування менша поточної", "");
            jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            isExpired = true;
        }
        return isExpired;
    }

    private boolean isContainsInProcessedUsers(List<ProcessedUser> processedUsers, String login) {
        ProcessedUser user = processedUsers.stream()
                .filter(processedUser -> processedUser.getAdLogin().equals(login))
                .findFirst()
                .orElse(null);
        if (user != null) {
            return true;
        }
        return false;
    }

}
