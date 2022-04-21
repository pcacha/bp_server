package cz.zcu.students.cacha.bp_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a show-case with exhibits
 */
@Entity
@Data
@NoArgsConstructor
public class Showcase {

    /**
     * id - primary key
     */
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    /**
     * name of show-case
     */
    @NotNull(message = "Name can not be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 to 100 letters long")
    @Column(length = 100)
    private String name;

    /**
     * description of show-case
     */
    @Size(max = 350, message = "Description must be maximally 350 letters long")
    @Column(length = 350)
    private String description;

    /**
     * room where is show-case located
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnore
    private Room room;

    /**
     * all exhibits in show-case
     */
    @OneToMany(mappedBy = "showcase", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Exhibit> exhibits;

    /**
     * all images
     */
    @OneToMany(mappedBy = "showcase", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private Set<ShowcaseImage> images;

    /**
     * registration date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdAt;

    /**
     * sets parameters for newly created show-cases
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    /**
     * Gets whether two instances represents the same show-case
     * @param o comparing show-case
     * @return whether two instances represents the same show-case
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Showcase)) return false;
        Showcase that = (Showcase) o;
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
