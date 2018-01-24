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
import com.ivkos.microbloggr.post.services.PostLikeService;
import com.ivkos.microbloggr.post.services.PostService;
import com.ivkos.microbloggr.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Service
class AuthenticationPrincipalAwarePostLikeAugmentationCapablePostServiceProxy implements PostService
{
    private final PostService postService;
    private final PostLikeService postLikeService;

    @Autowired
    AuthenticationPrincipalAwarePostLikeAugmentationCapablePostServiceProxy(PostService postService, PostLikeService postLikeService)
    {
        this.postService = postService;
        this.postLikeService = postLikeService;
    }

    @Override
    @Transactional(readOnly = true)
    public Post findById(UUID id)
    {
        return augment(postService.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsByUser(User user)
    {
        return postService.getPostsByUser(user)
            .stream()
            .map(this::augment)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsByMultipleUsers(Collection<User> users)
    {
        return postService.getPostsByMultipleUsers(users)
            .stream()
            .map(this::augment)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Post createPost(User author, String content, UUID pictureId)
    {
        return augment(postService.createPost(author, content, pictureId));
    }

    @Override
    @Transactional(readOnly = true)
    public Post updatePost(Post post)
    {
        return augment(postService.updatePost(post));
    }

    @Override
    public void deletePost(Post post)
    {
        postService.deletePost(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getFeedForUser(User user)
    {
        return postService.getFeedForUser(user)
            .stream()
            .map(this::augment)
            .collect(Collectors.toList());
    }

    private Post augment(Post post)
    {
        User principal = (User) getPrincipal();
        if (principal == null) return post;

        long likes = postLikeService.getLikeCountOfPost(post);
        boolean isLiked = postLikeService.doesLike(principal, post);

        post.setLikes(likes);
        post.setLiked(isLiked);

        return post;
    }

    private Object getPrincipal()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getPrincipal() : null;
    }
}
