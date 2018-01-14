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

package com.ivkos.microbloggr.post.services.impl;

import com.ivkos.microbloggr.post.models.Post;
import com.ivkos.microbloggr.post.models.PostLike;
import com.ivkos.microbloggr.post.models.PostLikeId;
import com.ivkos.microbloggr.post.repositories.PostLikeRepository;
import com.ivkos.microbloggr.post.services.PostLikeService;
import com.ivkos.microbloggr.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class PostLikeServiceImpl implements PostLikeService
{
    private final PostLikeRepository likeRepository;

    @Autowired
    PostLikeServiceImpl(PostLikeRepository likeRepository)
    {
        this.likeRepository = likeRepository;
    }

    @Override
    public Optional<PostLike> find(User liker, Post post)
    {
        return Optional.ofNullable(likeRepository.findOne(new PostLikeId(liker, post)));
    }

    @Override
    public PostLike likePost(User actor, Post post)
    {
        return find(actor, post)
            .orElseGet(() -> likeRepository.save(new PostLike(actor, post)));
    }

    @Override
    public void unlikePost(User actor, Post post)
    {
        if (!doesLike(actor, post)) return;
        likeRepository.delete(new PostLikeId(actor, post));
    }

    @Override
    public boolean doesLike(User viewer, Post post)
    {
        return likeRepository.exists(new PostLikeId(viewer, post));
    }

    @Override
    public List<User> getLikersOfPost(Post post)
    {
        return likeRepository.findByIdPostOrderByCreatedAtDesc(post)
            .map(PostLike::getLiker)
            .collect(Collectors.toList());
    }

    @Override
    public long getLikeCountOfPost(Post post)
    {
        return likeRepository.countByIdPost(post);
    }
}
