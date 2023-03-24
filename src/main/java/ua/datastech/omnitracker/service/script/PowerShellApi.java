package ua.datastech.omnitracker.service.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class PowerShellApi implements PowerShellExecutor {

    private static final String COMMAND_IDENTITY = "powershell.exe -command Get-ADUser -Identity ";
    private static final String FILE_COMMAND_HEADER = "powershell.exe -command $User = \"oschadbank\\SVC.00.OIMUser\"\n" +
            "$PWord = ConvertTo-SecureString -String \"&sLN~t&q302T9r?\" -AsPlainText -Force\n" +
            "$Credential = New-Object -TypeName System.Management.Automation.PSCredential -ArgumentList $User, $PWord\n" +
            "Import-Module ActiveDirectory\n";

    @Override
    public void execute() {
        Process powerShellProcess = null;
        try {
            powerShellProcess = Runtime.getRuntime().exec(FILE_COMMAND_HEADER);
            powerShellProcess = Runtime.getRuntime().exec(COMMAND_IDENTITY + "panokiv");

            powerShellProcess.getOutputStream().close();
            String line;

            BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));

            while ((line = stdout.readLine()) != null) {
                log.info("PowerShell output: " + line);
            ***REMOVED***
            stdout.close();

            BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));

            while ((line = stderr.readLine()) != null) {
                log.warn("PowerShell error: " + line);
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

    ***REMOVED***

***REMOVED***
