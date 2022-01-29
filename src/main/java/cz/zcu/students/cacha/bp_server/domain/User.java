package cz.zcu.students.cacha.bp_server.domain;

import cz.zcu.students.cacha.bp_server.validators.UniqueUsername;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.*;

@Entity
@Data
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "Username can not be blank")
    @Size(min = 3, max = 30, message = "Username must be between 3 to 30 letters long")
    @UniqueUsername
    private String username;

    @NotNull(message = "Password can not be blank")
    @Size(min = 8, max = 50, message = "Password must be between 8 to 50 letters long")
    @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="Password must contain a lowercase and an uppercase letter and a number")
    private String password;

    @NotNull(message = "E-mail can not be blank")
    @Email(message = "Bad e-mail format")
    @Size(min = 1, max = 50, message = "E-mail must be maximally 50 letters long")
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private Boolean banned;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Translation> translations;

    @ManyToOne(fetch=FetchType.LAZY)
    private Institution institution;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        banned = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !banned;
    }

    public boolean isTranslator() {
        return getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains(ROLE_TRANSLATOR);
    }

    public boolean isInstitutionOwner() {
        return getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains(ROLE_INSTITUTION_OWNER);
    }

    public boolean isAdmin() {
        return getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains(ROLE_ADMIN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
