package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.Building;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * view model describing a building
 */
@Data
@NoArgsConstructor
public class BuildingVM {
    /**
     * building id
     */
    private Long buildingId;
    /**
     * building name
     */
    private String name;
    /**
     * building description
     */
    private String description;
    /**
     * id of institution where is building located
     */
    private Long institutionId;
    /**
     * building registration date
     */
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createdAt;

    /**
     * creates new instance based on given building
     * @param building building
     */
    public BuildingVM(Building building) {
        buildingId = building.getId();
        name = building.getName();
        description = building.getDescription();
        institutionId = building.getInstitution().getId();
        createdAt = building.getCreatedAt();
    }
}
