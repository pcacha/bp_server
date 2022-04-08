package cz.zcu.students.cacha.bp_server.view_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.zcu.students.cacha.bp_server.domain.Room;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * view model describing a room
 */
@Data
@NoArgsConstructor
public class RoomVM {
    /**
     * room id
     */
    private Long roomId;
    /**
     * room name
     */
    private String name;
    /**
     * room description
     */
    private String description;
    /**
     * id of building where is room located
     */
    private Long buildingId;
    /**
     * room registration date
     */
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date createdAt;

    /**
     * creates new instance based on given room
     * @param room room
     */
    public RoomVM(Room room) {
        roomId = room.getId();
        name = room.getName();
        description = room.getDescription();
        buildingId = room.getBuilding().getId();
        createdAt = room.getCreatedAt();
    }
}
