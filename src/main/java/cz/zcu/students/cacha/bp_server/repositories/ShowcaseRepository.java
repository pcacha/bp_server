package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Room;
import cz.zcu.students.cacha.bp_server.domain.Showcase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Class represent repository which is responsible for show-case db operations
 */
@Repository
public interface ShowcaseRepository extends JpaRepository<Showcase, Long> {

    /**
     * Gets showcase by its name and room
     * @param name showcase name
     * @param room room
     * @return found showcase
     */
    Optional<Showcase> findByNameAndRoom(String name, Room room);
}
