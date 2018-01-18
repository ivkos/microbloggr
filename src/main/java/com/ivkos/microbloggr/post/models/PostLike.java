/*
 * Copyright 2018 Ivaylo Stoyanov <me@ivkos.com>
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

package com.ivkos.microbloggr.post.models;

import com.ivkos.microbloggr.user.models.User;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.Instant;

import static java.time.Instant.now;

@Entity
public class PostLike
{
    @EmbeddedId
    private PostLikeId id;

    @Column
    private Instant createdAt = now();

    PostLike() {}

    public PostLike(User liker, Post post)
    {
        this.id = new PostLikeId(liker, post);
    }

    public PostLikeId getId()
    {
        return id;
    }

    public User getLiker()
    {
        return id.getLiker();
    }

    public Post getPost()
    {
        return id.getPost();
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }
}
