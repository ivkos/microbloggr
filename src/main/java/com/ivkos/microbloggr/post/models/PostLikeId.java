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

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PostLikeId implements Serializable
{
    @OneToOne
    @JoinColumn(name = "liker_id")
    private User liker;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    PostLikeId() {}

    public PostLikeId(User liker, Post post)
    {
        this.liker = liker;
        this.post = post;
    }

    public User getLiker()
    {
        return liker;
    }

    public PostLikeId setLiker(User liker)
    {
        this.liker = liker;
        return this;
    }

    public Post getPost()
    {
        return post;
    }

    public PostLikeId setPost(Post post)
    {
        this.post = post;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getLiker().getId(), getPost().getId());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof PostLikeId)) return false;
        PostLikeId that = (PostLikeId) o;
        return Objects.equals(getLiker().getId(), that.getLiker().getId()) &&
            Objects.equals(getPost().getId(), that.getPost().getId());
    }
}
