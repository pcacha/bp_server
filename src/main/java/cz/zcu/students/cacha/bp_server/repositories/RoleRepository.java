package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    // TODO
}
