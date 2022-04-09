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
 * Represents a building belonging to a cultural institution
 */
@Entity
@Data
@NoArgsConstructor
public class Building {

    /**
     * id - primary key
     */
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    /**
     * name of building
     */
    @NotNull(message = "Name can not be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 to 100 letters long")
    @Column(length = 100)
    private String name;

    /**
     * description of building
     */
    @Size(max = 350, message = "Description must be maximally 350 letters long")
    @Column(length = 350)
    private String description;

    /**
     * institution owning building
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnore
    private Institution institution;

    /**
     * all rooms of building
     */
    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private Set<Room> rooms;

    /**
     * all exhibits in building
     */
    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Exhibit> exhibits;

    /**
     * registration date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdAt;

    /**
     * sets parameters for newly created buildings
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    /**
     * Gets whether two instances represents the same building
     * @param o comparing building
     * @return whether two instances represents the same building
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Building)) return false;
        Building that = (Building) o;
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
