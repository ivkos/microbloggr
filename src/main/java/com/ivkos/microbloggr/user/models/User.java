/*
 * Copyright 2017 Ivaylo Stoyanov <me@ivkos.com>
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

package com.ivkos.microbloggr.user.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ivkos.microbloggr.support.Role;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static java.lang.String.format;

@Entity
@Table(name = "users")
public class User implements UserDetails
{
    public static final User ANONYMOUS = new User(UUID.randomUUID());
    public static final String REGEX_VANITY =
        "^([A-Za-z0-9_](?:(?:[A-Za-z0-9_]|(?:\\.(?!\\.))){0,28}(?:[A-Za-z0-9_]))?)$";

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column
    @JsonIgnore
    private boolean enabled = true;

    @Column
    @JsonIgnore
    private boolean admin = false;

    @Column(unique = true)
    @JsonIgnore
    @Email
    private String email;

    @JsonIgnore
    private String password;

    @Column(unique = true)
    @Pattern(regexp = REGEX_VANITY)
    private String vanity;

    @Column
    private String name;

    User() {}

    User(UUID id)
    {
        this.id = id;
    }

    public User(String email, String password, String vanity)
    {
        this.email = email;
        this.password = password;
        this.vanity = vanity;
    }

    public UUID getId()
    {
        return id;
    }

    public String getVanity()
    {
        return vanity;
    }

    public User setVanity(String vanity)
    {
        this.vanity = vanity;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public User setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getEmail()
    {
        return email;
    }

    public User setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public boolean isAdmin()
    {
        return admin;
    }

    public User setAdmin(boolean admin)
    {
        this.admin = admin;
        return this;
    }

    @Override
    public String toString()
    {
        return format("[@%s] %s <%s>", vanity, name, email);
    }

    //region UserDetails boilerplate
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        Collection<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority(Role.USER));

        if (admin) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN));
        }

        return authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword()
    {
        return password;
    }

    public User setPassword(String password)
    {
        this.password = password;
        return this;
    }

    @Override
    @JsonIgnore
    public String getUsername()
    {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled()
    {
        return enabled;
    }

    public User setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }
    //endregion
}
