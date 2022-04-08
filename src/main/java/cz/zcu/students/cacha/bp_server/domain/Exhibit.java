package cz.zcu.students.cacha.bp_server.domain;

import cz.zcu.students.cacha.bp_server.validators.PngJpgFile;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static cz.zcu.students.cacha.bp_server.assets_store_config.WebConfiguration.DEFAULT_EXHIBIT_IMAGE;

/**
 * Represents an exhibit of a cultural institution
 */
@Entity
@Data
@NoArgsConstructor
public class Exhibit {

    /**
     * id - primary key
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * name of exhibit
     */
    @NotNull(message = "Name can not be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 to 100 letters long")
    @Column(length = 100)
    private String name;

    /**
     * name of image of exhibit in fs
     */
    @Column(length = 100)
    private String image = DEFAULT_EXHIBIT_IMAGE;

    /**
     * base64 encoded image of exhibit
     */
    @Transient
    @PngJpgFile
    private String encodedImage;

    /**
     * text of info label
     */
    @NotNull(message = "Info label text can not be blank")
    @Size(max = 25000, message = "The text of an information label must be maximally 25000 letters long")
    @Column(length = 25000, columnDefinition="TEXT")
    private String infoLabelText;

    /**
     * name of image of info label in fs
     */
    @Column(length = 100)
    private String infoLabel;

    /**
     * base64 encoded info label
     */
    @Transient
    @PngJpgFile
    @NotNull(message = "Informational label of an exhibit can not be blank")
    private String encodedInfoLabel;

    /**
     * id of building where is exhibit located
     */
    @Transient
    @Pattern(regexp="^\\d+$", message="Building id must be a positive integer")
    private String buildingId;

    /**
     * id of room where is exhibit located
     */
    @Transient
    @Pattern(regexp="^\\d+$", message="Room id must be a positive integer")
    private String roomId;

    /**
     * id of show-case where is exhibit located
     */
    @Transient
    @Pattern(regexp="^\\d+$", message="Show-case id must be a positive integer")
    private String showcaseId;

    /**
     * all translations matching to exhibit
     */
    @OneToMany(mappedBy = "exhibit", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private Set<Translation> translations;

    /**
     * institution owning exhibit
     */
    @ManyToOne(fetch=FetchType.LAZY)
    private Institution institution;

    /**
     * building where is exhibit located
     */
    @ManyToOne(fetch=FetchType.LAZY)
    private Building building;

    /**
     * room where is exhibit located
     */
    @ManyToOne(fetch=FetchType.LAZY)
    private Room room;

    /**
     * show-case where is exhibit located
     */
    @ManyToOne(fetch=FetchType.LAZY)
    private Showcase showcase;

    /**
     * register date of exhibit
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /**
     * saves registration date on new exhibit save
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    /**
     * Defines whether two exhibit instances are the same exhibit
     * @param o comparing object
     * @return whether two exhibit instances are the same exhibit
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exhibit)) return false;
        Exhibit exhibit = (Exhibit) o;
        return id.equals(exhibit.id);
    }

    /**
     * Gets the hash code of exhibit
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
