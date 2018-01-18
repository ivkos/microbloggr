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
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
public class UserSession
{
    public static final long SESSION_VALIDITY_DAYS = 6 * 30;

    @Id
    @GeneratedValue
    @JsonProperty("sessionId")
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = true)
    private Instant expiresAt;

    @Column(nullable = true)
    @JsonIgnore
    private Instant lastSeenAt;

    UserSession() {}

    UserSession(UUID id, User user)
    {
        this(user);
        this.id = id;
    }

    public UserSession(User user)
    {
        this.user = user;

        Instant now = Instant.now();
        this.createdAt = now;
        this.expiresAt = now.plus(Duration.ofDays(SESSION_VALIDITY_DAYS));
    }

    public UUID getId()
    {
        return id;
    }

    public User getUser()
    {
        return user;
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public Instant getExpiresAt()
    {
        return expiresAt;
    }

    public UserSession setExpiresAt(Instant expiresAt)
    {
        this.expiresAt = expiresAt;
        return this;
    }

    public Instant getLastSeenAt()
    {
        return lastSeenAt;
    }

    public UserSession setLastSeenAt(Instant lastSeenAt)
    {
        this.lastSeenAt = lastSeenAt;
        return this;
    }

    @JsonIgnore
    public boolean isExpired()
    {
        if (expiresAt == null) return false;
        return expiresAt.isBefore(Instant.now());
    }
}
