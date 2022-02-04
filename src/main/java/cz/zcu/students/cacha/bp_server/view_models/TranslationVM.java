package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class TranslationVM {

    private Long translationId;
    private Long institutionId;
    private String authorUsername;
    private String translatedText;
    private Boolean isOfficial;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date createdAt;
    private Integer likesCount;

    public TranslationVM(Translation translation) {
        translationId = translation.getId();
        institutionId = translation.getExhibit().getInstitution().getId();
        authorUsername = translation.getAuthor().getUsername();
        translatedText = translation.getText();
        isOfficial = translation.getIsOfficial();
        createdAt = translation.getCreatedAt();
        likesCount = translation.getLikers().size();
    }
}
