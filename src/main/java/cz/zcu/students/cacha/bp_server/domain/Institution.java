package cz.zcu.students.cacha.bp_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

/**
 * Class represents a cultural institution
 */
@Entity
@Data
@NoArgsConstructor
public class Institution {

    /**
     * id - primary key
     */
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    /**
     * name of institution
     */
    @NotNull(message = "Name can not be blank")
    @Size(min = 3, max = 100, message = "Name must be between 3 to 100 letters long")
    @UniqueInstitutionName
    @Column(length = 100)
    private String name;

    /**
     * address of institution
     */
    @NotNull(message = "Address can not be blank")
    @Size(min = 3, max = 100, message = "Address must be between 3 to 100 letters long")
    @Column(length = 100)
    private String address;

    /**
     * description of institution
     */
    @NotNull(message = "Description can not be blank")
    @Size(min = 15, max = 350, message = "Description must be between 15 to 350 letters long")
    @Column(length = 350)
    private String description;

    /**
     * name of image of institution in fs
     */
    @Column(length = 100)
    @JsonIgnore
    private String image = DEFAULT_INSTITUTION_IMAGE;

    /**
     * base64 encoded image
     */
    @Transient
    @PngJpgFile
    private String encodedImage;

    /**
     * geographical latitude
     */
    @JsonIgnore
    private Double latitude;

    /**
     * latitude string for user input
     */
    @Transient
    @NotNull(message = "Latitude can not be empty")
    @Size(max = 10, message = "Latitude can be maximally 10 digits long")
    @Pattern(regexp="^(\\+|-)?(?:90(?:(?:\\.0+)?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]+)?))$", message="Latitude format is incorrect, example: +90.0")
    private String latitudeString;

    /**
     * geographical longitude
     */
    @JsonIgnore
    private Double longitude;

    /**
     * longitude string for user input
     */
    @Transient
    @NotNull(message = "Longitude can not be empty")
    @Size(max = 10, message = "Longitude can be maximally 10 digits long")
    @Pattern(regexp="^(\\+|-)?(?:180(?:(?:\\.0+)?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]+)?))$", message="Longitude format is incorrect, example: -127.55")
    private String longitudeString;

    /**
     * suppotred languages for translation
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name = "institutions_languages",
            joinColumns = @JoinColumn(name = "institution_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "language_id", referencedColumnName = "id"))
    @JsonIgnore
    private Set<Language> languages = new HashSet<>();

    /**
     * managers (owners) of institution
     */
    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> owners;

    /**
     * all owned exhibits
     */
    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private Set<Exhibit> exhibits;

    /**
     * all owned buildings
     */
    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private Set<Building> buildings;

    /**
     * all images
     */
    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private Set<InstitutionImage> images;

    /**
     * registration date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdAt;

    /**
     * distance for ordering institutions
     */
    @Transient
    @JsonIgnore
    private Double distance;

    /**
     * sets parameters for newly registered institutions
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    /**
     * Gets whether two instances represents the same institution
     * @param o comparing institution
     * @return whether two instances represents the same institution
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Institution)) return false;
        Institution that = (Institution) o;
        return id.equals(that.id);
    }

    /**
     * Gets the hash code for given instance
     * @return hash code for given instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
