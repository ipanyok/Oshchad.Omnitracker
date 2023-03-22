package ua.datastech.omnitracker.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;
import ua.datastech.omnitracker.service.tracker.api.OmnitrackerApiService;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerJob {

    private final OmnitrackerApiService omnitrackerApiService;
    private final JdbcQueryService jdbcQueryService;

    // todo think about transactions and try/catch sections (when send closure)
//    @Transactional
    @Scheduled(cron = "*/10 * * * * *") // todo 10 min
    public void saveOmniDataToOIM() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedRequests();

        omniData.forEach(oimUserDto -> {
            if (!oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerPickupService(oimUserDto.getEmpNumber(), null, oimUserDto.getObjectId());
            ***REMOVED***

            List<Long> ids = jdbcQueryService.findOimUserByEmpNumber(oimUserDto.getEmpNumber());

            if (ids.isEmpty()) {
                log.info("User [empNumber=" + oimUserDto.getEmpNumber() + "] wasn't found.");

                jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));

                if (!oimUserDto.getIsClosureSent()) {
                    omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), null, oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відмовлено", "Користувач [empNumber=" + oimUserDto.getEmpNumber() + "] не знайдений в системі ОІМ.");
                ***REMOVED***
            ***REMOVED*** else {
                Integer updateCount = jdbcQueryService.updateOimUser(oimUserDto);
                if (updateCount != 0) {
                    jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_SAVED", "1"));
                    log.info("User[empNumber=" + oimUserDto.getEmpNumber() + "] data was saved in OIM");
                ***REMOVED***
            ***REMOVED***
        ***REMOVED***);
    ***REMOVED***

    @Transactional
    @Scheduled(cron = "*/10 * * * * *") // todo 10 min
    public void processRebranching() {
        List<OimUserDto> rebranchedUsers = jdbcQueryService.findOimUnprocessedUsers();

        rebranchedUsers.forEach(oimUserDto -> {

            List<OimUserDto> omniData = jdbcQueryService.findOmniUnprocessedRequests(oimUserDto.getEmpNumber(), oimUserDto.getObjectId());

            omniData.forEach(o -> {
                if (!o.getIsClosureSent()) {
                    // todo do something
                    jdbcQueryService.updateOimUserEndDate(o.getEmpNumber());
                    //
                    omnitrackerApiService.callOmniTrackerClosureService(o.getEmpNumber(), null, o.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, "Вирішено", "");
                ***REMOVED***
                jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            ***REMOVED***);

        ***REMOVED***);
    ***REMOVED***

    @Scheduled(cron = "@daily")
    public void cleanupData() {
        List<OimUserDto> rebranchedUsers = jdbcQueryService.findOimUsersToClean();
        rebranchedUsers.forEach(oimUserDto -> {
            Integer execute = jdbcQueryService.updateOimUserByUsrKey(oimUserDto.getUsrKey());
            if (execute != 0) {
                log.info("User[empNumber=" + oimUserDto.getEmpNumber() + "] was cleaned up");
            ***REMOVED***
        ***REMOVED***);

    ***REMOVED***

***REMOVED***
