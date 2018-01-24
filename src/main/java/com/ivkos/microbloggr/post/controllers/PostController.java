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

package com.ivkos.microbloggr.post.controllers;

import com.ivkos.microbloggr.post.models.Post;
import com.ivkos.microbloggr.post.services.PostLikeService;
import com.ivkos.microbloggr.post.services.PostService;
import com.ivkos.microbloggr.support.forms.request.CreatePostRequestForm;
import com.ivkos.microbloggr.user.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
class PostController
{
    private final PostService postService;
    private final PostLikeService likeService;

    PostController(PostService postService, PostLikeService likeService)
    {
        this.postService = postService;
        this.likeService = likeService;
    }

    @GetMapping("/{id}")
    Post getById(@PathVariable UUID id)
    {
        return postService.findById(id);
    }

    @PostMapping
    Post createPost(@Valid @RequestBody CreatePostRequestForm form, @AuthenticationPrincipal User author)
    {
        return postService.createPost(author, form.type, form.content);
    }

    @DeleteMapping("/{id}")
    ResponseEntity deletePost(@PathVariable UUID id, @AuthenticationPrincipal User actor)
    {
        Post post = postService.findById(id);

        if (actor.isAdmin() || post.getAuthor().getId().equals(actor.getId())) {
            postService.deletePost(post);

            return ResponseEntity.noContent().build();
        }

        throw new AccessDeniedException("You cannot delete this post");
    }

    @GetMapping("/{id}/likes")
    List<User> getPostLikers(@PathVariable UUID id)
    {
        return likeService.getLikersOfPost(postService.findById(id));
    }

    @PutMapping("/{id}/likes")
    ResponseEntity likePost(@PathVariable UUID id, @AuthenticationPrincipal User actor)
    {
        likeService.likePost(actor, postService.findById(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/likes")
    ResponseEntity unlikePost(@PathVariable UUID id, @AuthenticationPrincipal User actor)
    {
        likeService.unlikePost(actor, postService.findById(id));
        return ResponseEntity.noContent().build();
    }
}
