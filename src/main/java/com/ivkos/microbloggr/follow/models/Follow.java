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

package com.ivkos.microbloggr.follow.models;

import com.ivkos.microbloggr.user.models.User;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "follows")
public class Follow
{
    @EmbeddedId
    private FollowId id;

    @Column
    private Instant createdAt = Instant.now();

    Follow() { }

    public Follow(User follower, User followee)
    {
        this.id = new FollowId(follower, followee);
    }

    public FollowId getId()
    {
        return id;
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public User getFollower()
    {
        return id.getFollower();
    }

    public User getFollowee()
    {
        return id.getFollowee();
    }
}
