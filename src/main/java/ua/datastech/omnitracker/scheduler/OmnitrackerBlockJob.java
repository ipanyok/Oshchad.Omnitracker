package ua.datastech.omnitracker.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Scheduled(cron = "0 0/10 * * * ?")
    public void processData() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedBlockRequests();
        omniData.forEach(oimUserDto -> {

            if (checkIfDateExpired(oimUserDto)) {
                return;
            ***REMOVED***

            if (!oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
            ***REMOVED***

            List<OimUserDto> usersDataToBlock = jdbcQueryService.findUsersToProcess(LocalDateTime.now(), oimUserDto.getObjectId());
            List<ProcessedUser> users = null;
            if (oimUserDto.getAction().equals(ActionType.DISABLE_USER.name())) {
                users = jdbcQueryService.findUsersToBlockByEmployeeNumber(usersDataToBlock.stream().map(OimUserDto::getEmpNumber).collect(Collectors.toList()));
            ***REMOVED***
            if (oimUserDto.getAction().equals(ActionType.ENABLE_USER.name())) {
                users = jdbcQueryService.findUsersToEnableByEmployeeNumber(usersDataToBlock.stream().map(OimUserDto::getEmpNumber).collect(Collectors.toList()));
            ***REMOVED***
            if (oimUserDto.getAction().equals(ActionType.DISABLE_REGION.name())) {
                List<String> sourceIds = usersDataToBlock.stream().map(OimUserDto::getSourceId).collect(Collectors.toList());
                users = new ArrayList<>();
                for (String sourceId : sourceIds) {
                    String parentOrgCode = jdbcQueryService.checkSourceId(sourceId);
                    if (parentOrgCode.equals(sourceId)) {
                        users.addAll(jdbcQueryService.findUsersToBlockBySourceIdIfEqual(sourceId));
                    ***REMOVED*** else {
                        users.addAll(jdbcQueryService.findUsersToBlockBySourceIdIfNotEqual(sourceId));
                    ***REMOVED***
                ***REMOVED***
            ***REMOVED***
            if (oimUserDto.getAction().equals(ActionType.ENABLE_REGION.name())) {
                List<String> sourceIds = usersDataToBlock.stream().map(OimUserDto::getSourceId).collect(Collectors.toList());
                users = new ArrayList<>();
                for (String sourceId : sourceIds) {
                    String parentOrgCode = jdbcQueryService.checkSourceId(sourceId);
                    if (parentOrgCode.equals(sourceId)) {
                        users.addAll(jdbcQueryService.findUsersToEnableBySourceIdIfEqual(sourceId));
                    ***REMOVED*** else {
                        users.addAll(jdbcQueryService.findUsersToEnableBySourceIdIfNotEqual(sourceId));
                    ***REMOVED***
                ***REMOVED***
            ***REMOVED***

            if ((usersDataToBlock != null && !usersDataToBlock.isEmpty()) && (users == null || users.isEmpty())) {
                omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Не знайдено жодного користувача в AD.", "");
                jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            ***REMOVED***
            if (users != null && !users.isEmpty()) {
                List<String> unprocessedUsers = powerShellExecutor.execute(oimUserDto.getAction(), users);
                String result;
                if (unprocessedUsers.isEmpty()) {
                    result = "Вирішено";
                ***REMOVED*** else {
                    result = "Вирішено. \nНе оброблені користувачі: \n" + unprocessedUsers;
                ***REMOVED***
                omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, result, "");
                jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            ***REMOVED***
        ***REMOVED***);
    ***REMOVED***

    @Scheduled(cron = "0 0/10 * * * ?")
    public void processAttachmentsData() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedAttachmentsRequests();
        omniData.forEach(oimUserDto -> {

            if (checkIfDateExpired(oimUserDto)) {
                return;
            ***REMOVED***

            List<OimUserDto> attachmentsToSave = jdbcQueryService.findAttachmentToSave(oimUserDto.getObjectId());
            if (attachmentsToSave != null && !attachmentsToSave.isEmpty()) {
                if (!oimUserDto.getIsPickupSent()) {
                    attachmentsToSave.forEach(attachment -> {
                        String attachmentString = omnitrackerApiService.callOmniTrackerGetAttachmentService(attachment.getOid(), attachment.getObjectId());
                        Integer execute = jdbcQueryService.updateAttachments(attachment.getId(), attachmentString);
                        if (execute != 0) {
                            log.info("Attachment for request " + oimUserDto.getObjectId() + " was decoded.");
                        ***REMOVED***
                    ***REMOVED***);
                    omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
                    oimUserDto.setIsPickupSent(true);
                ***REMOVED***
            ***REMOVED***

            List<OimUserDto> attachmentData = jdbcQueryService.findAttachment(LocalDateTime.now(), oimUserDto.getObjectId());

            AtomicBoolean isProcessed = new AtomicBoolean(false);
            AtomicReference<List<String>> unprocessedUsers = new AtomicReference<>();
            attachmentData.forEach(data -> {
                List<String> users = ExcelFileReader.read(data.getAttachment());
                List<ProcessedUser> processedUsers;
                if (users != null && !users.isEmpty()) {
                    if (data.getAction().equals(ActionType.ENABLE_BY_FILE.name())) {
                        processedUsers = jdbcQueryService.findUsersToEnableByAdLogin(users);
                    ***REMOVED*** else {
                        processedUsers = jdbcQueryService.findUsersToDisableByAdLogin(users);
                    ***REMOVED***
                    List<String> existingUnprocessedUsers = unprocessedUsers.get();
                    List<String> scriptUnprocessedUsers = powerShellExecutor.execute(data.getAction(), processedUsers);
                    if (existingUnprocessedUsers == null) {
                        unprocessedUsers.set(scriptUnprocessedUsers);
                    ***REMOVED*** else {
                        existingUnprocessedUsers.addAll(scriptUnprocessedUsers);
                        unprocessedUsers.set(existingUnprocessedUsers);
                    ***REMOVED***
                    isProcessed.set(true);
                ***REMOVED***
            ***REMOVED***);
            if (isProcessed.get()) {
                String result;
                if (unprocessedUsers.get().isEmpty()) {
                    result = "Вирішено";
                ***REMOVED*** else {
                    result = "Вирішено. \nНе оброблені користувачі: \n" + unprocessedUsers;
                ***REMOVED***
                omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, result, "");
                jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            ***REMOVED*** else if ((attachmentData != null && !attachmentData.isEmpty()) && oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Не знайдено жодного користувача в AD або вкладення не містить логінів.", "");
                jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            ***REMOVED***
        ***REMOVED***);
    ***REMOVED***

    private boolean checkIfDateExpired(OimUserDto oimUserDto) {
        boolean isExpired = false;
        if (LocalDateTime.parse(oimUserDto.getActionDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).truncatedTo(MINUTES).isBefore(LocalDateTime.now().truncatedTo(MINUTES)) && !oimUserDto.getIsClosureSent()) {
            if (!oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
            ***REMOVED***
            omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Дата блокування менша поточної", "");
            jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            isExpired = true;
        ***REMOVED***
        return isExpired;
    ***REMOVED***

***REMOVED***
