package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewTranslationVM {
    private String exhibitName;
    private String infoLabel;
    private String translatedText;

    public NewTranslationVM(Exhibit exhibit, String translatedText) {
        exhibitName = exhibit.getName();
        infoLabel = exhibit.getInfoLabel();
        this.translatedText = translatedText;
    }
}
