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
import com.ivkos.microbloggr.post.models.PostType;
import com.ivkos.microbloggr.post.services.PostService;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class UserPostController
{
    private final UserService userService;
    private final PostService postService;

    UserPostController(UserService userService, PostService postService)
    {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/users/{id}/posts")
    List<Post> getPostsByUser(@PathVariable UUID id)
    {
        return postService.getPostsByUser(userService.findById(id));
    }

    @GetMapping("/users/{id}/posts/{type}")
    List<Post> getPostsByUserByType(@PathVariable UUID userId, @PathVariable PostType type)
    {
        User user = userService.findById(userId);
        return postService.getPostsByUserByType(user, type);
    }
}
