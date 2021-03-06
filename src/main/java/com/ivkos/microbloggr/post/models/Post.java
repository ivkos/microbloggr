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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ivkos.microbloggr.picture.models.Picture;
import com.ivkos.microbloggr.user.models.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    @Column
    private String content;

    @OneToOne
    @JoinColumn(name = "picture_id")
    @JsonIgnore
    private Picture picture;

    @Transient
    private Long likes;

    @Transient
    private Boolean isLiked;

    Post() {}

    public Post(User author, String content)
    {
        this.author = author;
        this.content = content;
    }

    public Post(User author, Picture picture)
    {
        this.author = author;
        this.picture = picture;
    }

    public Post(User author, String content, Picture picture)
    {
        this.author = author;
        this.content = content;
        this.picture = picture;
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

    @Transient
    public String getPictureId()
    {
        return picture != null ? picture.getId().toString() : null;
    }
}
