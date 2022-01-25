package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExhibitVM {
    private Long exhibitId;
    private String name;
    private String image;

    public ExhibitVM(Exhibit exhibit) {
        exhibitId = exhibit.getId();
        name = exhibit.getName();
        image = exhibit.getImage();
    }
}
