package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.validators.UniqueInstitutionNameExclUpdated;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * view model for updating institution information
 */
@Data
@NoArgsConstructor
public class UpdateInstitutionVM {

    /**
     * institution name
     */
    @NotNull(message = "Name can not be blank")
    @Size(min = 3, max = 100, message = "Name must be between 3 to 100 letters long")
    @UniqueInstitutionNameExclUpdated
    private String name;

    /**
     * institution address
     */
    @NotNull(message = "Address can not be blank")
    @Size(min = 3, max = 100, message = "Address must be between 3 to 100 letters long")
    private String address;

    /**
     * description of institution
     */
    @NotNull(message = "Description can not be blank")
    @Size(min = 15, max = 350, message = "Description must be between 15 to 350 letters long")
    private String description;

    /**
     * string with latitude
     */
    @NotNull(message = "Latitude can not be empty")
    @Size(max = 10, message = "Latitude can be maximally 10 digits long")
    @Pattern(regexp="^(\\+|-)?(?:90(?:(?:\\.0+)?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]+)?))$", message="Latitude format is incorrect, example: +90.0")
    private String latitudeString;

    /**
     * string with longitude
     */
    @NotNull(message = "Longitude can not be empty")
    @Size(max = 10, message = "Longitude can be maximally 10 digits long")
    @Pattern(regexp="^(\\+|-)?(?:180(?:(?:\\.0+)?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]+)?))$", message="Longitude format is incorrect, example: -127.55")
    private String longitudeString;
}
