package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class BooleanValVM {
    @NotNull(message = "Value can not be empty")
    private Boolean value;
}