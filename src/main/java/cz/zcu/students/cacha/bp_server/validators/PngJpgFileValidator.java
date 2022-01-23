package cz.zcu.students.cacha.bp_server.validators;

import cz.zcu.students.cacha.bp_server.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;

public class PngJpgFileValidator implements ConstraintValidator<PngJpgFile, String> {

    @Autowired
    private FileService fileService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }

        // byte[] decodeBytes = Base64.getMimeDecoder().decode(value.replace("\n", "").trim());
        byte[] decodeBytes = Base64.getDecoder().decode(value);
        String fileType = fileService.detectType(decodeBytes);
        if(fileType.equalsIgnoreCase("image/png") || fileType.equalsIgnoreCase("image/jpeg")) {
            return true;
        }

        return false;
    }
}