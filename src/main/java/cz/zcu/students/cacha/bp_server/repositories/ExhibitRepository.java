package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhibitRepository extends JpaRepository<Exhibit, Long> {
    // TODO
}
