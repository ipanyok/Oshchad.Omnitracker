package ua.datastech.omnitracker.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;
import ua.datastech.omnitracker.service.parse.ExcelFileReader;
import ua.datastech.omnitracker.service.tracker.api.OmnitrackerApiService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerBlockJob {

    private final OmnitrackerApiService omnitrackerApiService;
    private final JdbcQueryService jdbcQueryService;

    // todo think about transactions and try/catch sections (when send closure)
//    @Transactional
    @Scheduled(cron = "*/10 * * * * *") // todo 10 min
    public void processData() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedBlockRequests();

        omniData.forEach(oimUserDto -> {
            if (!oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
            ***REMOVED***

            List<OimUserDto> usersToBlock = jdbcQueryService.findUsersToBlock(LocalDate.now());
            usersToBlock.forEach(data -> {
                // todo get users from data
                List<String> users = Arrays.asList(data.getAdLogin());
                // todo block user
                users.forEach(user -> log.info("User " + user + " was blocked"));
                //
                omnitrackerApiService.callOmniTrackerClosureService(null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, "Вирішено", "");
                jdbcQueryService.updateOmniBlockRequestQuery(oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            ***REMOVED***);
        ***REMOVED***);
    ***REMOVED***

    @Scheduled(cron = "*/10 * * * * *") // todo 10 min
    public void processAttachmentsData() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedAttachmentsRequests();

        omniData.forEach(oimUserDto -> {
            if (!oimUserDto.getIsPickupSent()) {
                String attachmentString = omnitrackerApiService.callOmniTrackerGetAttachmentService(oimUserDto.getOid(), oimUserDto.getObjectId());
                Integer execute = jdbcQueryService.updateAttachments(oimUserDto.getId(), attachmentString);
                if (execute != 0) {
                    log.info("Attachment for request " + oimUserDto.getObjectId() + " was decoded.");
                ***REMOVED***
                omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getObjectId());
            ***REMOVED***

            List<OimUserDto> attachmentData = jdbcQueryService.findAttachment(LocalDate.now());
            attachmentData.forEach(data -> {
                List<String> users = ExcelFileReader.read(data.getAttachment());
                // todo block user
                users.forEach(user -> log.info("User " + user + " was blocked"));
                //
                omnitrackerApiService.callOmniTrackerClosureService(null, data.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, "Вирішено", "");
                jdbcQueryService.updateOmniBlockRequestQuery(data.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            ***REMOVED***);
        ***REMOVED***);
    ***REMOVED***

***REMOVED***
