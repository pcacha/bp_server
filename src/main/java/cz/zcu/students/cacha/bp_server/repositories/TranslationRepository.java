package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    @Query(
            value = "select distinct on (exhibit_id, language_id) " +
                    "* from translation " +
                    "where user_id = :user_id " +
                    "order by created_at desc",
            nativeQuery = true
    )
    Set<Translation> getSequences(@Param("user_id") Long user_id);

    @Query(
            value = "delete from translation " +
                    "where user_id = :user_id " +
                    "and exhibit_id = :exhibit_id " +
                    "and language_id = :language_id",
            nativeQuery = true
    )
    void deleteSequence(@Param("user_id") Long user_id, @Param("exhibit_id") Long exhibit_id, @Param("language_id") Long language_id);

    @Query(
            value = "select * from translation " +
                    "where user_id = :user_id " +
                    "and exhibit_id = :exhibit_id " +
                    "and language_id = :language_id " +
                    "order by created_at desc",
            nativeQuery = true
    )
    Set<Translation> getSequence(@Param("user_id") Long user_id, @Param("exhibit_id") Long exhibit_id, @Param("language_id") Long language_id);

    @Query(
            value = "delete from translation " +
                    "where user_id = :user_id " +
                    "and exhibit_id = :exhibit_id " +
                    "and language_id = :language_id " +
                    "and created_at > :created_at",
            nativeQuery = true
    )
    void rollback(@Param("user_id") Long user_id, @Param("exhibit_id") Long exhibit_id, @Param("language_id") Long language_id, @Param("created_at") Date created_at);

    @Query(
            value = "select top 1 " +
                    "* from translation " +
                    "where user_id = :user_id " +
                    "and exhibit_id = :exhibit_id " +
                    "and language_id = :language_id " +
                    "order by created_at desc",
            nativeQuery = true
    )
    Optional<Translation> getLatestTranslation(@Param("user_id") Long user_id, @Param("exhibit_id") Long exhibit_id, @Param("language_id") Long language_id);

    @Query(
            value = "select distinct on (author_id) " +
                    "* from translation " +
                    "where exhibit_id = :exhibit_id " +
                    "and language_id = :language_id " +
                    "order by created_at desc",
            nativeQuery = true
    )
    Set<Translation> getLatestTranslations(@Param("exhibit_id") Long exhibit_id, @Param("language_id") Long language_id);

    @Query(
            value = "select top 1 " +
                    "* from translation " +
                    "where exhibit_id = :exhibit_id " +
                    "and language_id = :language_id " +
                    "and is_official = 1",
            nativeQuery = true
    )
    Optional<Translation> getOfficialTranslation(@Param("exhibit_id") Long exhibit_id, @Param("language_id") Long language_id);
}
