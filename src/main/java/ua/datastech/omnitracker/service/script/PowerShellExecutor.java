package ua.datastech.omnitracker.service.script;

import ua.datastech.omnitracker.model.oim.ProcessedUser;

import java.util.List;

public interface PowerShellExecutor {

    List<String> execute(String action, List<ProcessedUser> users);

}
