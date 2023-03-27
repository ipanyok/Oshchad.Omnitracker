package ua.datastech.omnitracker.service.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class PowerShellLocal implements PowerShellExecutor {

    @Override
    public void execute(String action, List<String> adLogins) {
        if (action.equals("DISABLE")) {
            adLogins.forEach(s -> log.info("User " + s + " was blocked"));
        ***REMOVED***
        if (action.equals("ENABLE")) {
            adLogins.forEach(s -> log.info("User " + s + " was enabled"));
        ***REMOVED***
    ***REMOVED***
***REMOVED***