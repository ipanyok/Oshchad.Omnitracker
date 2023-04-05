package ua.datastech.omnitracker.service.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.ActionType;

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

    private static final String COMMAND = "powershell.exe -command  ***REMOVED***"& {$username = 'oschadbank***REMOVED******REMOVED***%s'; $password = ConvertTo-SecureString '%s' -AsPlainText -Force; $credential = New-Object System.Management.Automation.PSCredential($username, $password); Import-Module ActiveDirectory; %s %s -Credential $credential ***REMOVED******REMOVED***"***REMOVED***n";

    @Override
    public List<String> execute(String action, List<String> adLogins) {
        List<String> unprocessedUsers = new ArrayList<>();
        if (action.equals(ActionType.DISABLE_USER.name()) || action.equals(ActionType.DISABLE_REGION.name()) || action.equals(ActionType.DISABLE_BY_FILE.name())) {
            adLogins.forEach(login -> runPowerShell(String.format(COMMAND, psUser, psPassword, commandDisable, login), login, unprocessedUsers));
        ***REMOVED***
        if (action.equals(ActionType.ENABLE_USER.name()) || action.equals(ActionType.ENABLE_REGION.name()) || action.equals(ActionType.ENABLE_BY_FILE.name())) {
            adLogins.forEach(login -> runPowerShell(String.format(COMMAND, psUser, psPassword, commandEnable, login), login, unprocessedUsers));
        ***REMOVED***
        return unprocessedUsers;
    ***REMOVED***

    private List<String> runPowerShell(String script, String adLogin, List<String> unprocessedUsers) {
        Process powerShellProcess = null;
        try {
            powerShellProcess = Runtime.getRuntime().exec(script);
            powerShellProcess.getOutputStream().close();

            BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
            if (stderr.readLine() == null) {
                log.info("User " + adLogin + " was processed.");
            ***REMOVED*** else {
                log.error(String.format("Can't processed user [%s]", adLogin));
                unprocessedUsers.add(adLogin);
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
