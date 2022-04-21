package cz.zcu.students.cacha.bp_server.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Represents an image of a show-case
 */
@Entity
@Data
@NoArgsConstructor
public class ShowcaseImage {

    /**
     * id - primary key
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * path to image in fs
     */
    @Column(length = 300)
    private String path;

    /**
     * description of image
     */
    @Column(length = 350)
    private String description;

    /**
     * show-case owning image
     */
    @ManyToOne(fetch=FetchType.LAZY)
    private Showcase showcase;

    /**
     * upload date
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /**
     * sets parameters for newly created images
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    /**
     * Gets whether two instances represents the same image
     * @param o comparing image
     * @return whether two instances represents the same image
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShowcaseImage)) return false;
        ShowcaseImage that = (ShowcaseImage) o;
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
