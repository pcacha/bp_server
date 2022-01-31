package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserDetailVM {
    private Long userId;
    private String username;
    private String email;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date createdAt;
    private Boolean isBanned;
    private Boolean isTranslator;
    private Boolean isInstitutionOwner;
    private String institutionName;

    public UserDetailVM(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.isBanned = user.getBanned();
        this.isTranslator = user.isTranslator();
        this.isInstitutionOwner = user.isInstitutionOwner();

        if(user.getInstitution() != null) {
            this.institutionName = user.getInstitution().getName();
        }
    }
}
