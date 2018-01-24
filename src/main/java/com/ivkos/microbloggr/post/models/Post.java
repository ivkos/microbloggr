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

package com.ivkos.microbloggr.post.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ivkos.microbloggr.user.models.User;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

import static java.time.Instant.now;

@Entity
@Table(name = "posts")
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Post
{
    @Id
    @GeneratedValue
    private UUID id;

    private Instant createdAt = now();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column
    private PostType type;

    @Column
    private String content;

    @Transient
    private Long likes;

    @Transient
    private Boolean isLiked;

    Post() {}

    public Post(User author, PostType type, String content)
    {
        this.author = author;
        this.type = type;
        this.content = content;
    }

    public UUID getId()
    {
        return id;
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public User getAuthor()
    {
        return author;
    }

    public PostType getType()
    {
        return type;
    }

    public String getContent()
    {
        return content;
    }

    public Post setContent(String content)
    {
        this.content = content;
        return this;
    }

    public Long getLikes()
    {
        return likes;
    }

    public Post setLikes(Long likes)
    {
        this.likes = likes;
        return this;
    }

    public Boolean getLiked()
    {
        return isLiked;
    }

    public Post setLiked(Boolean liked)
    {
        isLiked = liked;
        return this;
    }
}
