package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    Set<Language> findAllByOrderByName();
}
