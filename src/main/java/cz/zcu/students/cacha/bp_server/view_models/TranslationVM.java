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
    private String authorUsername;
    private String translatedText;
    private Boolean isOfficial;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createdAt;

    public TranslationVM(Translation translation) {
        translationId = translation.getId();
        authorUsername = translation.getAuthor().getUsername();
        translatedText = translation.getText();
        isOfficial = translation.getIsOfficial();
        createdAt = translation.getCreatedAt();
    }
}
