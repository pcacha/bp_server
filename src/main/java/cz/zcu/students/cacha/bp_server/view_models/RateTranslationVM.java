package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class RateTranslationVM {

    private Long translationId;
    private String authorUsername;
    private String translatedText;
    private Boolean isOfficial;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date createdAt;
    private Integer likesCount;
    private Boolean liked;

    public RateTranslationVM(Translation translation, User user) {
        translationId = translation.getId();
        authorUsername = translation.getAuthor().getUsername();
        translatedText = translation.getText();
        isOfficial = translation.getIsOfficial();
        createdAt = translation.getCreatedAt();
        likesCount = translation.getLikers().size();
        liked = translation.getLikers().stream().anyMatch(u -> u.getId().equals(user.getId()));
    }
}
