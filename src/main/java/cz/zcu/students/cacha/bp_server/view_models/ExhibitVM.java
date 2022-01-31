package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ExhibitVM {
    private Long exhibitId;
    private String name;
    private String image;
    private String building;
    private String room;
    private String showcase;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date createdAt;

    public ExhibitVM(Exhibit exhibit) {
        exhibitId = exhibit.getId();
        name = exhibit.getName();
        image = exhibit.getImage();
        building = exhibit.getBuilding();
        room = exhibit.getRoom();
        showcase = exhibit.getShowcase();
        createdAt = exhibit.getCreatedAt();
    }
}
