package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class CoordinatesVM {
    @NotNull(message = "Latitude can not be empty")
    @Pattern(regexp="^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$", message="Latitude format is incorrect, example: +90.0")
    private String latitude;

    @NotNull(message = "Longitude can not be empty")
    @Pattern(regexp="^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$", message="Longitude format is incorrect, example: -127.55")
    private String longitude;
}
