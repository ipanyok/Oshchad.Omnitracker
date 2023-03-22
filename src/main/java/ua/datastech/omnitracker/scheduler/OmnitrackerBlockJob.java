package ua.datastech.omnitracker.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;
import ua.datastech.omnitracker.service.tracker.api.OmnitrackerApiService;

import java.time.LocalDate;
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
    public void saveOmniDataToOIM() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedBlockRequests();

        omniData.forEach(oimUserDto -> {
            if (!oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerPickupService(null, oimUserDto.getAdLogin(), oimUserDto.getObjectId());
            ***REMOVED***

            List<OimUserDto> usersToBlock = jdbcQueryService.findUsersToBlock(LocalDate.now());
            usersToBlock.forEach(user -> {
                // todo block user
                if (true) { // blocked
                    log.info("User " + user.getAdLogin() + " was blocked");
                    omnitrackerApiService.callOmniTrackerClosureService(null, user.getAdLogin(), user.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, "Вирішено", "");
                ***REMOVED*** else { // if non blocked (ad login not found)
                    omnitrackerApiService.callOmniTrackerClosureService(null, user.getAdLogin(), user.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відмовлено", "Користувач [empNumber=" + oimUserDto.getEmpNumber() + "] не знайдений в системі ОІМ.");
                ***REMOVED***
                jdbcQueryService.updateOmniBlockRequestQuery(user.getAdLogin(), user.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
            ***REMOVED***);


        ***REMOVED***);
    ***REMOVED***

***REMOVED***
