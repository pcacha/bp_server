package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Building;
import cz.zcu.students.cacha.bp_server.domain.Institution;
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

    /**
     * Gets building by its id and institution
     * @param buildingId building id
     * @param institution institution
     * @return building by its id and institution
     */
    Optional<Building> findByIdAndInstitution(Long buildingId, Institution institution);
}
