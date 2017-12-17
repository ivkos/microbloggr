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

package com.ivkos.microbloggr.follow.controllers;

import com.ivkos.microbloggr.follow.services.FollowService;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
class FollowController
{
    private final UserService userService;
    private final FollowService followService;

    @Autowired
    FollowController(UserService userService, FollowService followService)
    {
        this.userService = userService;
        this.followService = followService;
    }

    @GetMapping("/users/{id}/followers")
    List<User> getFollowersOfUser(@PathVariable UUID id)
    {
        User user = userService.findById(id);
        return followService.getFollowersOfUser(user);
    }

    @GetMapping("/users/{id}/followees")
    List<User> getFolloweesOfUser(@PathVariable UUID id)
    {
        User user = userService.findById(id);
        return followService.getFolloweesOfUser(user);
    }

    @PutMapping("/users/{id}/followers")
    ResponseEntity followUser(@PathVariable UUID id, @AuthenticationPrincipal User currentUser)
    {
        User followee = userService.findById(id);
        followService.create(currentUser, followee);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}/followers")
    ResponseEntity unfollowUser(@PathVariable UUID id, @AuthenticationPrincipal User currentUser)
    {
        User followee = userService.findById(id);
        followService.unfollow(currentUser, followee);
        return ResponseEntity.noContent().build();
    }
}
