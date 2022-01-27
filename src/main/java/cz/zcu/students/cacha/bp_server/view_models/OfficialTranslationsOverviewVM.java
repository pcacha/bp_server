package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OfficialTranslationsOverviewVM {
    private Set<LanguageVM> languages;
    private Set<ExhibitVM> exhibits;

    public OfficialTranslationsOverviewVM(Set<Language> languages, Set<Exhibit> exhibits) {
        this.languages = languages.stream().map(LanguageVM::new).collect(Collectors.toSet());
        this.exhibits = exhibits.stream().map(ExhibitVM::new).collect(Collectors.toSet());
    }
}
