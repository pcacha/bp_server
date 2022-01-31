package cz.zcu.students.cacha.bp_server.validators;

import cz.zcu.students.cacha.bp_server.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static cz.zcu.students.cacha.bp_server.services.FileService.separator;

public class PngJpgFileValidator implements ConstraintValidator<PngJpgFile, String> {

    @Autowired
    private FileService fileService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }

        // byte[] decodeBytes = Base64.getMimeDecoder().decode(value.replace("\n", "").trim());
        String fileType;
        try {
            if (value.contains(separator)) {
                value = value.split(separator)[1];
            }

            byte[] decodeBytes = Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8));
            fileType = fileService.detectType(decodeBytes);
        } catch (Exception e) {
            return false;
        }

        if(fileType.equalsIgnoreCase("image/png") || fileType.equalsIgnoreCase("image/jpeg")) {
            return true;
        }

        return false;
    }
}