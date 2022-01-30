package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.validators.PngJpgFile;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ImageVM {

    @PngJpgFile
    @NotNull(message = "Image can not be null")
    private String encodedImage;
}
