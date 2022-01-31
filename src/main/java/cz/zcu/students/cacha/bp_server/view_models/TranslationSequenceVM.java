package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Language;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class TranslationSequenceVM {

    private Long exhibitId;
    private Long languageId;
    private String institutionName;
    private String exhibitName;
    private String language;
    private String exhibitImage;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date latestTranslationCreatedAt;

    public TranslationSequenceVM(Translation translation) {
        Exhibit ex = translation.getExhibit();
        Language lang = translation.getLanguage();

        exhibitId = ex.getId();
        languageId = lang.getId();
        institutionName = ex.getInstitution().getName();
        exhibitName = ex.getName();
        language = lang.getName();
        exhibitImage = ex.getImage();
        latestTranslationCreatedAt = translation.getCreatedAt();
    }
}
