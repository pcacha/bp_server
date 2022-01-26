package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UpdateExhibitVM {

    @NotNull(message = "Name can not be blank")
    @Size(min = 3, max = 255, message = "Name must be between 3 to 255 letters long")
    private String name;

    @NotNull(message = "Building can not be blank")
    @Size(min = 0, max = 255, message = "Building must be maximally 255 letters long")
    private String building;

    @NotNull(message = "Room can not be blank")
    @Size(min = 0, max = 255, message = "Room must be maximally 255 letters long")
    private String room;

    @NotNull(message = "Show-case can not be blank")
    @Size(min = 0, max = 255, message = "Show-case must be maximally 255 letters long")
    private String showcase;
}
