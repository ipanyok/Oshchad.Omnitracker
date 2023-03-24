package ua.datastech.omnitracker.service.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class PowerShellLocal implements PowerShellExecutor {
    @Override
    public void execute() {
        log.info("User blocked");
    ***REMOVED***
***REMOVED***
