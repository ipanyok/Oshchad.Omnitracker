package ua.datastech.omnitracker.service.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.datastech.omnitracker.model.dto.ActionType;
import ua.datastech.omnitracker.model.oim.ProcessedUser;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class PowerShellLocal implements PowerShellExecutor {

    @Override
    public List<String> execute(String action, List<ProcessedUser> users) {
        if (action.equals(ActionType.DISABLE_USER.name()) || action.equals(ActionType.DISABLE_REGION.name()) || action.equals(ActionType.DISABLE_BY_FILE.name())) {
            users.forEach(s -> log.info("User " + s.getAdLogin() + " was blocked"));
        }
        if (action.equals(ActionType.ENABLE_USER.name()) || action.equals(ActionType.ENABLE_REGION.name()) || action.equals(ActionType.ENABLE_BY_FILE.name())) {
            users.forEach(s -> log.info("User " + s.getAdLogin() + " was enabled"));
        }
        return new ArrayList<>();
    }
}
