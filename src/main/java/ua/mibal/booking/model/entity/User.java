/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.mibal.booking.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.NumericBooleanConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.mibal.booking.model.embeddable.Phone;
import ua.mibal.booking.model.embeddable.Photo;
import ua.mibal.booking.model.embeddable.Role;
import ua.mibal.booking.model.embeddable.UserSettings;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;
import static ua.mibal.booking.model.embeddable.Role.GLOBAL_MANAGER;
import static ua.mibal.booking.model.embeddable.Role.LOCAL_MANAGER;
import static ua.mibal.booking.model.embeddable.Role.USER;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded
    private Phone phone;

    @Embedded
    private Photo photo;

    @Embedded
    private UserSettings userSettings = UserSettings.DEFAULT;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(nullable = false)
    private ZonedDateTime creationDateTime = ZonedDateTime.now();

    @ElementCollection
    @CollectionTable(
            name = "roles",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "roles_user_id_fk")
            ),
            indexes = @Index(name = "roles_user_id_idx", columnList = "user_id")
    )
    @Setter(PRIVATE)
    private Set<Role> roles = new HashSet<>(Set.of(USER));

    @ManyToMany(mappedBy = "managers")
    @Setter(PRIVATE)
    private Set<Hotel> hotels = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Setter(PRIVATE)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Setter(PRIVATE)
    private List<Comment> comments = new ArrayList<>();

    public boolean is(Role role) {
        return getRoles().contains(role);
    }

    public void addRole(Role role) {
        if (role.equals(GLOBAL_MANAGER)) {
            this.roles.add(LOCAL_MANAGER);
        }
        this.roles.add(role);
    }

    public void deleteRole(Role role) {
        if (role.equals(USER)) return;
        if (role.equals(LOCAL_MANAGER)) {
            if (this.is(GLOBAL_MANAGER)) {
                this.roles.remove(GLOBAL_MANAGER);
            }
            this.hotels.forEach(hotel -> hotel.removeManager(this));
            this.hotels.clear();
        }
        this.roles.remove(role);
    }

    public void addHotel(Hotel hotel) {
        hotel.addManager(this);
    }

    public void removeHotel(Hotel hotel) {
        hotel.removeManager(this);
    }

    public void addReservation(Reservation reservation) {
        reservation.setUser(this);
        this.reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        if (this.reservations.contains(reservation)) {
            this.reservations.remove(reservation);
            reservation.setUser(null);
        }
    }

    public void addComment(Comment comment) {
        comment.setUser(this);
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        if (this.comments.contains(comment)) {
            this.comments.remove(comment);
            comment.setUser(null);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getEmail() != null && Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return enabled;
    }
}