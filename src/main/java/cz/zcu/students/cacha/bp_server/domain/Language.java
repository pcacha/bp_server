package cz.zcu.students.cacha.bp_server.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Language {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 100)
    private String name;

    @Column(length = 2)
    private String code;

    @ManyToMany(mappedBy = "languages", fetch= FetchType.LAZY)
    private Set<Institution> institutions;

    public Language(String name, String code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Language)) return false;
        Language language = (Language) o;
        return id.equals(language.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
