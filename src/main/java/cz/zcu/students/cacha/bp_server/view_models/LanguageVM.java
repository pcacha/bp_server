package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LanguageVM {
    private Long languageId;
    private String name;
    private String code;

    public LanguageVM(Language language) {
        languageId = language.getId();
        name = language.getName();
        code = language.getCode();
    }
}
