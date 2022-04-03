package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.Institution;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * view model for institution
 */
@Data
@NoArgsConstructor
public class InstitutionVM {
    /**
     * institution id
     */
    private Long institutionId;
    /**
     * institution name
     */
    private String name;
    /**
     * institution address
     */
    private String address;
    /**
     * description of institution
     */
    private String description;
    /**
     * institution image name
     */
    private String image;
    /**
     * institution latitude
     */
    private Double latitude;
    /**
     * institution longitude
     */
    private Double longitude;
    /**
     * institution registration date
     */
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date createdAt;

    /**
     * Creates new instance based on given institution
     * @param institution institution
     */
    public InstitutionVM(Institution institution) {
        institutionId = institution.getId();
        name = institution.getName();
        address = institution.getAddress();
        description = institution.getDescription();
        image = institution.getImage();
        latitude = institution.getLatitude();
        longitude = institution.getLongitude();
        createdAt = institution.getCreatedAt();
    }
}
