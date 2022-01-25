package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TranslationTextVM {

    private String translatedText;

    public TranslationTextVM(String translatedText) {
        this.translatedText = translatedText;
    }
}