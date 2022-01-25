package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AllowedLanguagesVM {

    private Set<LanguageVM> possibleLanguages;
    private Set<LanguageVM> chosenLanguages;

    public AllowedLanguagesVM(Set<Language> possibleLanguages, Set<Language> chosenLanguages) {
        this.possibleLanguages = possibleLanguages.stream().map(LanguageVM::new).collect(Collectors.toSet());
        this.chosenLanguages = chosenLanguages.stream().map(LanguageVM::new).collect(Collectors.toSet());
    }
}
