package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OfficialTranslationsVM {

    private Set<TranslationVM> translations;
    private ExhibitVM exhibit;

    public OfficialTranslationsVM(Set<Translation> translations, Exhibit exhibit) {
        this.translations = translations.stream().map(TranslationVM::new).collect(Collectors.toSet());
        this.exhibit = new ExhibitVM(exhibit);
    }
}
