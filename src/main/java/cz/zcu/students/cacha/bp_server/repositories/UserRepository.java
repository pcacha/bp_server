package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String userName);

    @Query(
            value = "select * from user u " +
                    "where 'ROLE_ADMIN' not in (" +
                    "select r.name from role r " +
                    "join users_roles ur on r.id = ur.role_id " +
                    "where ur.user_id = u.id);",
            nativeQuery = true)
    Set<User> getNonAdminUsers();

    // TODO
}
