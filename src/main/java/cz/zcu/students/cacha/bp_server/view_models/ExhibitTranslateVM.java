package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ExhibitTranslateVM {
    private Long exhibitId;
    private String name;
    private String infoLabelText;
    private String infoLabel;
    private Set<LanguageVM> supportedLanguages;

    public ExhibitTranslateVM(Exhibit exhibit) {
        exhibitId = exhibit.getId();
        infoLabelText = exhibit.getInfoLabelText();
        name = exhibit.getName();
        infoLabel = exhibit.getInfoLabel();
        supportedLanguages = exhibit.getInstitution().getLanguages().stream().map(LanguageVM::new).collect(Collectors.toSet());
    }
}
