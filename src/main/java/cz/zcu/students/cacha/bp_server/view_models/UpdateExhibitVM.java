package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * view model for updating an exhibit
 */
@Data
@NoArgsConstructor
public class UpdateExhibitVM {

    /**
     * exhibit name
     */
    @NotNull(message = "Name can not be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 to 100 letters long")
    private String name;

    /**
     * info label text
     */
    @NotNull(message = "Info label text can not be blank")
    @Size(max = 25000, message = "The text of an information label must be maximally 25000 letters long")
    private String infoLabelText;

    /**
     * id of building where is exhibit located
     */
    @Pattern(regexp="^\\d+$", message="Building id must be a positive integer")
    private String buildingId;

    /**
     * id of room where is exhibit located
     */
    @Pattern(regexp="^\\d+$", message="Room id must be a positive integer")
    private String roomId;

    /**
     * id of show-case where is exhibit located
     */
    @Pattern(regexp="^\\d+$", message="Show-case id must be a positive integer")
    private String showcaseId;
}
