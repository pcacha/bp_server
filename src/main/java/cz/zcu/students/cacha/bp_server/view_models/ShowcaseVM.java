package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.Showcase;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * view model describing a show-case
 */
@Data
@NoArgsConstructor
public class ShowcaseVM {
    /**
     * show-case id
     */
    private Long showcaseId;
    /**
     * show-case name
     */
    private String name;
    /**
     * show-case description
     */
    private String description;
    /**
     * id of room where is show-case located
     */
    private Long roomId;
    /**
     * show-case registration date
     */
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date createdAt;

    /**
     * creates new instance based on given show-case
     * @param showcase show-case
     */
    public ShowcaseVM(Showcase showcase) {
        showcaseId = showcase.getId();
        name = showcase.getName();
        description = showcase.getDescription();
        roomId = showcase.getRoom().getId();
        createdAt = showcase.getCreatedAt();
    }
}
