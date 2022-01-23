package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    // TODO
}
