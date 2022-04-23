package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * view model describing an exhibit
 */
@Data
@NoArgsConstructor
public class ExhibitVM {
    /**
     * exhibit id
     */
    private Long exhibitId;
    /**
     * exhibit name
     */
    private String name;
    /**
     * info label text
     */
    private String infoLabelText;
    /**
     * exhibit image name
     */
    private String image;
    /**
     * info label image name
     */
    private String infoLabel;
    /**
     * building
     */
    private BuildingVM building;
    /**
     * room
     */
    private RoomVM room;
    /**
     * showcase
     */
    private ShowcaseVM showcase;
    /**
     * exhibit registration date
     */
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createdAt;

    /**
     * creates new instance based on given exhibit
     * @param exhibit exhibit
     */
    public ExhibitVM(Exhibit exhibit) {
        exhibitId = exhibit.getId();
        name = exhibit.getName();
        infoLabelText = exhibit.getInfoLabelText();
        infoLabel = exhibit.getInfoLabel();
        image = exhibit.getImage();
        if(exhibit.getBuilding() != null) {
            building = new BuildingVM(exhibit.getBuilding());
        }
        if(exhibit.getRoom() != null) {
            room = new RoomVM(exhibit.getRoom());
        }
        if(exhibit.getShowcase() != null) {
            showcase = new ShowcaseVM(exhibit.getShowcase());
        }
        createdAt = exhibit.getCreatedAt();
    }
}
