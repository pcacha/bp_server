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
 * Represents a room inside a cultural institution
 */
@Entity
@Data
@NoArgsConstructor
public class Room {

    /**
     * id - primary key
     */
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    /**
     * name of room
     */
    @NotNull(message = "Name can not be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 to 100 letters long")
    @Column(length = 100)
    private String name;

    /**
     * description of room
     */
    @Size(max = 350, message = "Description must be maximally 350 letters long")
    @Column(length = 350)
    private String description;

    /**
     * building where is room located
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnore
    private Building building;

    /**
     * all show-cases of room
     */
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private Set<Showcase> showcases;

    /**
     * all exhibits in room
     */
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Exhibit> exhibits;

    /**
     * all images
     */
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private Set<RoomImage> images;

    /**
     * registration date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdAt;

    /**
     * sets parameters for newly created rooms
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    /**
     * Gets whether two instances represents the same room
     * @param o comparing room
     * @return whether two instances represents the same room
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room that = (Room) o;
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
