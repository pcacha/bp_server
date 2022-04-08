package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Class represent repository which is responsible for buildings db operations
 */
@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    /**
     * Gets building by its name
     * @param name building name
     * @return found building
     */
    Optional<Building> findByName(String name);
}
