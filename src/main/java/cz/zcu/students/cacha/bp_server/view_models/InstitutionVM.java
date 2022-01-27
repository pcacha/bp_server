package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstitutionVM {
    private Long institutionId;
    private String name;
    private String address;
    private String image;
    private Double latitude;
    private Double longitude;

    public InstitutionVM(Institution institution) {
        institutionId = institution.getId();
        name = institution.getName();
        address = institution.getAddress();
        image = institution.getImage();
        latitude = institution.getLatitude();
        longitude = institution.getLongitude();
    }
}
