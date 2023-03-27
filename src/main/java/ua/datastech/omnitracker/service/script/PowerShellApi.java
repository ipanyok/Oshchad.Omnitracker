package ua.datastech.omnitracker.service.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.ActionType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class PowerShellApi implements PowerShellExecutor {

    @Value("${power.shell.user***REMOVED***")
    private String psUser;

    @Value("${power.shell.password***REMOVED***")
    private String psPassword;

    private static final String COMMAND_DISABLE = "powershell.exe -command Get-ADUser -Identity ";
    private static final String COMMAND_ENABLE = "powershell.exe -command Get-ADUser -Identity ";
    private static final String CONNECT = "powershell.exe -command $User = ***REMOVED***"oschadbank***REMOVED******REMOVED***%s***REMOVED***"***REMOVED***n" +
            "$PWord = ConvertTo-SecureString -String ***REMOVED***"%s***REMOVED***" -AsPlainText -Force***REMOVED***n" +
            "$Credential = New-Object -TypeName System.Management.Automation.PSCredential -ArgumentList $User, $PWord***REMOVED***n" +
            "Import-Module ActiveDirectory***REMOVED***n";

    @Override
    public void execute(String action, List<String> adLogins) {
        Process powerShellProcess = null;
        try {
            powerShellProcess = Runtime.getRuntime().exec(String.format(CONNECT, psUser, psPassword));

            // todo return non blocked
            if (action.equals(ActionType.DISABLE_USER.name())) {
                adLogins.forEach(login -> runPowerShell(COMMAND_DISABLE, login));
            ***REMOVED***
            if (action.equals(ActionType.ENABLE_USER.name())) {
                adLogins.forEach(login -> runPowerShell(COMMAND_ENABLE, login));
            ***REMOVED***

        ***REMOVED*** catch (Exception e) {
            log.error("Error when run powershell command with ad module: ", e);
        ***REMOVED*** finally {
            try {
                powerShellProcess.getOutputStream().close();
            ***REMOVED*** catch (Exception ex) {
            ***REMOVED***
        ***REMOVED***

    ***REMOVED***

    private void runPowerShell(String script, String adLogin) {
        try {
            Process powerShellProcess = Runtime.getRuntime().exec(script + adLogin);
            powerShellProcess.getOutputStream().close();
            String line;

            BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
            if (stderr.readLine() == null) {
                log.info("User " + adLogin + " was processed.");
            ***REMOVED*** else {
                while ((line = stderr.readLine()) != null) {
                    log.warn("PowerShell error: " + line);
                ***REMOVED***
            ***REMOVED***
            stderr.close();
        ***REMOVED*** catch (Exception e) {
            throw new RuntimeException(e);
        ***REMOVED***

    ***REMOVED***

***REMOVED***
