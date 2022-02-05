package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Language;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class RateTranslationsVM {

    private List<RateTranslationVM> translations;
    private ExhibitVM exhibit;
    private String language;

    public RateTranslationsVM(Set<Translation> translations, Exhibit exhibit, User user, Language language) {
        this.translations = translations.stream().map(t -> new RateTranslationVM(t, user)).sorted(Comparator.comparing(RateTranslationVM::getCreatedAt).reversed())
                .collect(Collectors.toList());
        this.exhibit = new ExhibitVM(exhibit);
        this.language = language.getName();
    }
}
