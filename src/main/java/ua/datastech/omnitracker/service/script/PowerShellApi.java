package ua.datastech.omnitracker.service.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.ActionType;
import ua.datastech.omnitracker.model.oim.ProcessedUser;
import ua.datastech.omnitracker.service.jdbc.JdbcQueryService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"prod", "test"***REMOVED***)
public class PowerShellApi implements PowerShellExecutor {

    @Value("${powershell.user***REMOVED***")
    private String psUser;

    @Value("${powershell.password***REMOVED***")
    private String psPassword;

    @Value("${powershell.command.disable***REMOVED***")
    private String commandDisable;

    @Value("${powershell.command.enable***REMOVED***")
    private String commandEnable;

    private final JdbcQueryService jdbcQueryService;

    private static final String COMMAND = "powershell.exe -command  \"& {$username = 'oschadbank\\%s'; $password = ConvertTo-SecureString '%s' -AsPlainText -Force; $credential = New-Object System.Management.Automation.PSCredential($username, $password); Import-Module ActiveDirectory; %s %s -Credential $credential ***REMOVED***\"\n";

    @Override
    public List<String> execute(String action, List<ProcessedUser> users) {
        List<String> unprocessedUsers = new ArrayList<>();
        if (action.equals(ActionType.DISABLE_USER.name()) || action.equals(ActionType.DISABLE_REGION.name()) || action.equals(ActionType.DISABLE_BY_FILE.name())) {
            users.forEach(user -> runPowerShell(String.format(COMMAND, psUser, psPassword, commandDisable, user.getAdLogin()), user, unprocessedUsers));
        ***REMOVED***
        if (action.equals(ActionType.ENABLE_USER.name()) || action.equals(ActionType.ENABLE_REGION.name()) || action.equals(ActionType.ENABLE_BY_FILE.name())) {
            users.forEach(user -> runPowerShell(String.format(COMMAND, psUser, psPassword, commandEnable, user.getAdLogin()), user, unprocessedUsers));
        ***REMOVED***
        return unprocessedUsers;
    ***REMOVED***

    private List<String> runPowerShell(String script, ProcessedUser user, List<String> unprocessedUsers) {
        Process powerShellProcess = null;
        try {
            powerShellProcess = Runtime.getRuntime().exec(script);
            powerShellProcess.getOutputStream().close();

            BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
            if (stderr.readLine() == null) {
                log.info("User " + user.getAdLogin() + " was processed.");
                if (user.getProcessOimUserScript() != null) {
                    jdbcQueryService.processOimUser(user.getProcessOimUserScript());
                ***REMOVED***
                if (user.getProvisioningScript() != null) {
                    jdbcQueryService.processOimUser(user.getProvisioningScript());
                ***REMOVED***
            ***REMOVED*** else {
                log.error(String.format("Can't processed user [%s]", user.getAdLogin()));
                unprocessedUsers.add(user.getAdLogin());
            ***REMOVED***
            stderr.close();
        ***REMOVED*** catch (Exception e) {
            log.error("Error when run powershell command with ad module: ", e);
        ***REMOVED*** finally {
            try {
                powerShellProcess.getOutputStream().close();
            ***REMOVED*** catch (Exception ex) {
            ***REMOVED***
        ***REMOVED***
        return unprocessedUsers;
    ***REMOVED***

***REMOVED***
