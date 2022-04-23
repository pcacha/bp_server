package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * view model for passing coordinates
 */
@Data
@NoArgsConstructor
public class CoordinatesVM {
    /**
     * geographical latitude
     */
    @NotNull(message = "Latitude can not be empty")
    @Size(max = 10, message = "Latitude can be maximally 10 digits long")
    @Pattern(regexp="^(\\+|-)?(?:90(?:(?:\\.0+)?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]+)?))$", message="Latitude format is incorrect, example: +90.0")
    private String latitude;

    /**
     * geographical longitude
     */
    @NotNull(message = "Longitude can not be empty")
    @Size(max = 10, message = "Longitude can be maximally 10 digits long")
    @Pattern(regexp="^(\\+|-)?(?:180(?:(?:\\.0+)?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]+)?))$", message="Longitude format is incorrect, example: -127.55")
    private String longitude;
}
