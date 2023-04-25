package ua.datastech.omnitracker.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.OimUserDto;
import ua.datastech.omnitracker.model.omni.api.ResponseCodeEnum;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;
import ua.datastech.omnitracker.service.oim.OimClient;
import ua.datastech.omnitracker.service.tracker.api.OmnitrackerApiService;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnitrackerJob {

    private final OmnitrackerApiService omnitrackerApiService;
    private final JdbcQueryService jdbcQueryService;
    private final OimClient oimClient;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void saveOmniDataToOIM() {
        List<OimUserDto> omniData = jdbcQueryService.findAllUnprocessedRequests();

        omniData.forEach(oimUserDto -> {
            if (!oimUserDto.getIsPickupSent()) {
                omnitrackerApiService.callOmniTrackerPickupService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId());
            ***REMOVED***

            List<Long> ids = jdbcQueryService.findOimUserByEmpNumber(oimUserDto.getEmpNumber());

            if (ids.isEmpty()) {
                log.info("User [empNumber=" + oimUserDto.getEmpNumber() + "] wasn't found.");

                jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));

                if (!oimUserDto.getIsClosureSent()) {
                    omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Користувач [empNumber=" + oimUserDto.getEmpNumber() + "] не знайдений в системі ОІМ.", "");
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

//    @Scheduled(cron = "@daily")
    @Scheduled(cron = "0 0/15 * * * ?")
    public void processRebranching() {
        UserManager userManager = oimClient.getOIMClient().getService(UserManager.class);
        List<OimUserDto> rebranchedUsers = jdbcQueryService.findUsersForRebranching();
        List<OimUserDto> backUsers = jdbcQueryService.findUsersForBackToMainBranch();
        rebranching(userManager, rebranchedUsers, true);
        rebranching(userManager, backUsers, false);
    ***REMOVED***

    private void rebranching(UserManager userManager, List<OimUserDto> rebranchedUsers, boolean isRebranching) {
        String branchName = isRebranching ? "temp" : "main";
        rebranchedUsers.forEach(oimUserDto -> {
            String branch = isRebranching ? oimUserDto.getTmpBranch() : oimUserDto.getMainBranch();
            if (!oimUserDto.getIsClosureSent()) {
                User user = new User(String.valueOf(oimUserDto.getUsrKey()));
                user.setAttribute("CurrentBranch2", branch);
                log.info("Actualize user with key: " + oimUserDto.getUsrKey() + ". Current (" + branchName + ") branch is: " + branch);
                try {
                    userManager.modify(user);
                    omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_RESOLVED, "Вирішено", "");
                ***REMOVED*** catch (Exception e) {
                    log.error("Can't modify user with key: " + oimUserDto.getUsrKey(), e);
                    omnitrackerApiService.callOmniTrackerClosureService(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), ResponseCodeEnum.SC_CC_REJECTED, "Відхилено. Не можливо оновити користувача " + oimUserDto.getEmpNumber(), "");
                ***REMOVED***
            ***REMOVED***
            jdbcQueryService.updateOmniRequestQuery(oimUserDto.getEmpNumber(), oimUserDto.getObjectId(), Collections.singletonMap("IS_PROCESSED", "1"));
        ***REMOVED***);
    ***REMOVED***

***REMOVED***
