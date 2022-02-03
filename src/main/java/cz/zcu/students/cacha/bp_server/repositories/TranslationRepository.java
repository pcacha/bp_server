package cz.zcu.students.cacha.bp_server.repositories;

import cz.zcu.students.cacha.bp_server.domain.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Class represent repository which is responsible for translations db operations
 */
@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    /**
     * Gets all latest translations for every pair exhibit-languge for given translator
     * @param user_id translator id
     * @return latest translation for every pair exhibit-languge
     */
    @Query(
            value = "select * from translation " +
                    "where id in ( " +
                    "select max(id) from translation " +
                    "where author_id = :user_id " +
                    "group by exhibit_id, language_id) " +
                    "order by created_at desc",
            nativeQuery = true
    )
    List<Translation> getSequences(@Param("user_id") Long user_id);

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

    /**
     * Gets the latest translation of given author, exhibit and language
     * @param user_id author id
     * @param exhibit_id exhibit id
     * @param language_id language id
     * @return latest translation
     */
    @Query(
            value = "select * from translation " +
                    "where author_id = :user_id " +
                    "and exhibit_id = :exhibit_id " +
                    "and language_id = :language_id " +
                    "order by created_at desc " +
                    "limit 1",
            nativeQuery = true
    )
    Optional<Translation> getLatestTranslation(@Param("user_id") Long user_id, @Param("exhibit_id") Long exhibit_id, @Param("language_id") Long language_id);

    /**
     * Gets the latest translations for given exhibit and language from all authors
     * @param exhibit_id exhibit id
     * @param language_id language id
     * @return the latest translations for given exhibit and language from all authors
     */
    @Query(
            value = "select * from translation " +
                    "where id in ( " +
                    "select max(id) from translation " +
                    "where exhibit_id = :exhibit_id " +
                    "and language_id = :language_id " +
                    "group by author_id)",
            nativeQuery = true
    )
    Set<Translation> getLatestTranslations(@Param("exhibit_id") Long exhibit_id, @Param("language_id") Long language_id);

    /**
     * Gets the official translation for given exhibit and language
     * @param exhibit_id exhibit id
     * @param language_id language id
     * @return the official translation for given exhibit and language
     */
    @Query(
            value = "select * from translation " +
                    "where exhibit_id = :exhibit_id " +
                    "and language_id = :language_id " +
                    "and is_official = 1 " +
                    "limit 1",
            nativeQuery = true
    )
    Optional<Translation> getOfficialTranslation(@Param("exhibit_id") Long exhibit_id, @Param("language_id") Long language_id);
}
