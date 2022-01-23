package cz.zcu.students.cacha.bp_server.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Translation {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "Translation can not be empty")
    @Size(min = 10, max = 20000, message = "Translation must be between 10 to 20000 letters long")
    @Column(length = 20000)
    private String text;

    private Boolean isOfficial;

    @ManyToOne(fetch=FetchType.LAZY)
    private User author;

    @ManyToOne(fetch=FetchType.LAZY)
    private Language language;

    @ManyToOne(fetch=FetchType.LAZY)
    private Exhibit exhibit;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        isOfficial = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Translation)) return false;
        Translation that = (Translation) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
