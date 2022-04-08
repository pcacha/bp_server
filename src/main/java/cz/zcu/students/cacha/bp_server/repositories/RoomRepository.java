package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Building;
import cz.zcu.students.cacha.bp_server.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Class represent repository which is responsible for rooms db operations
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Gets room by its name and building
     * @param name room name
     * @param building building
     * @return found room
     */
    Optional<Room> findByNameAndBuilding(String name, Building building);
}
