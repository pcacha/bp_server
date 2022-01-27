package cz.zcu.students.cacha.bp_server.domain;

import cz.zcu.students.cacha.bp_server.validators.PngJpgFile;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static cz.zcu.students.cacha.bp_server.assets_store_config.WebConfiguration.DEFAULT_EXHIBIT_IMAGE;

@Entity
@Data
@NoArgsConstructor
public class Exhibit {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "Name can not be blank")
    @Size(min = 1, max = 255, message = "Name must be between 1 to 255 letters long")
    private String name;

    private String image = DEFAULT_EXHIBIT_IMAGE;

    @Transient
    @PngJpgFile
    private String encodedImage;

    @NotNull(message = "Info label text can not be blank")
    @Size(max = 15000, message = "The text of an information label must be maximally 15000 letters long")
    @Column(length = 15000)
    private String infoLabelText;

    private String infoLabel;

    @Transient
    @PngJpgFile
    @NotNull(message = "Informational label of an exhibit can not be blank")
    private String encodedInfoLabel;

    @NotNull(message = "Building can not be blank")
    @Size(max = 255, message = "Building must be maximally 255 letters long")
    private String building;

    @NotNull(message = "Room can not be blank")
    @Size(max = 255, message = "Room must be maximally 255 letters long")
    private String room;

    @NotNull(message = "Show-case can not be blank")
    @Size(max = 255, message = "Show-case must be maximally 255 letters long")
    private String showcase;

    @OneToMany(mappedBy = "exhibit", fetch = FetchType.LAZY)
    private Set<Translation> translations;

    @ManyToOne(fetch=FetchType.LAZY)
    private Institution institution;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exhibit)) return false;
        Exhibit exhibit = (Exhibit) o;
        return id.equals(exhibit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
