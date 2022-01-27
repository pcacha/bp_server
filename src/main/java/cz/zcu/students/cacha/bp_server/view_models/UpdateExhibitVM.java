package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UpdateExhibitVM {

    @NotNull(message = "Name can not be blank")
    @Size(min = 1, max = 255, message = "Name must be between 1 to 255 letters long")
    private String name;

    @NotNull(message = "Info label text can not be blank")
    @Size(max = 15000, message = "The text of an information label must be maximally 15000 letters long")
    private String infoLabelText;

    @NotNull(message = "Building can not be blank")
    @Size(max = 255, message = "Building must be maximally 255 letters long")
    private String building;

    @NotNull(message = "Room can not be blank")
    @Size(max = 255, message = "Room must be maximally 255 letters long")
    private String room;

    @NotNull(message = "Show-case can not be blank")
    @Size(max = 255, message = "Show-case must be maximally 255 letters long")
    private String showcase;
}
