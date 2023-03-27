package ua.datastech.omnitracker.model.oim;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AdditionalInfo {

    @EqualsAndHashCode.Include
    private String empNumber;
    private String mainBranch;
    private String tmpBranch;
    private String startDate;
    private String endDate;

    private String adLogin;
    private String action;
    private String actionDate;

    private String date;
    private List<Persons> persons;
***REMOVED***
