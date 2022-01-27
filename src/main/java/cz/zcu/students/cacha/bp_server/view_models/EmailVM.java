package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class EmailVM {

    @NotNull(message = "E-mail can not be blank")
    @Email(message = "Bad e-mail format")
    @Size(min = 1, max = 255, message = "E-mail must be maximally 255 letters long")
    private String email;
}
