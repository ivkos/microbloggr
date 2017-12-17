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

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowId implements Serializable
{
    @OneToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    @OneToOne
    @JoinColumn(name = "followee_id")
    private User followee;

    public FollowId() { }

    public FollowId(User follower, User followee)
    {
        this.follower = follower;
        this.followee = followee;
    }

    public User getFollower()
    {
        return follower;
    }

    public FollowId setFollower(User follower)
    {
        this.follower = follower;
        return this;
    }

    public User getFollowee()
    {
        return followee;
    }

    public FollowId setFollowee(User followee)
    {
        this.followee = followee;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(follower.getId(), followee.getId());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FollowId)) return false;
        FollowId followId = (FollowId) o;
        return Objects.equals(follower.getId(), followId.follower.getId()) &&
            Objects.equals(followee.getId(), followId.followee.getId());
    }
}
