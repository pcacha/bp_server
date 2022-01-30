package cz.zcu.students.cacha.bp_server.domain;

import cz.zcu.students.cacha.bp_server.validators.PngJpgFile;
import cz.zcu.students.cacha.bp_server.validators.UniqueInstitutionName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static cz.zcu.students.cacha.bp_server.assets_store_config.WebConfiguration.DEFAULT_INSTITUTION_IMAGE;

@Entity
@Data
@NoArgsConstructor
public class Institution {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "Name can not be blank")
    @Size(min = 3, max = 100, message = "Name must be between 3 to 100 letters long")
    @UniqueInstitutionName
    @Column(length = 100)
    private String name;

    @NotNull(message = "Address can not be blank")
    @Size(min = 3, max = 100, message = "Address must be between 3 to 100 letters long")
    @Column(length = 100)
    private String address;

    @Column(length = 100)
    private String image = DEFAULT_INSTITUTION_IMAGE;

    @Transient
    @PngJpgFile
    private String encodedImage;

    @NotNull(message = "Latitude can not be empty")
    @Pattern(regexp="^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$", message="Latitude format is incorrect, example: +90.0")
    private Double latitude;

    @NotNull(message = "Longitude can not be empty")
    @Pattern(regexp="^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$", message="Longitude format is incorrect, example: -127.55")
    private Double longitude;

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name = "institutions_languages",
            joinColumns = @JoinColumn(name = "institution_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "language_id", referencedColumnName = "id"))
    private Set<Language> languages = new HashSet<>();

    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY)
    private Set<User> owners;

    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY)
    private Set<Exhibit> exhibits;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Transient
    private Double distance;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Institution)) return false;
        Institution that = (Institution) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
