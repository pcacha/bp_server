package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewTranslationVM {
    private String exhibitName;
    private String languageName;
    private String infoLabelText;
    private String infoLabel;
    private String text;

    public NewTranslationVM(Exhibit exhibit, String text, String languageName) {
        exhibitName = exhibit.getName();
        infoLabelText = exhibit.getInfoLabelText();
        infoLabel = exhibit.getInfoLabel();
        this.text = text;
        this.languageName = languageName;
    }
}
