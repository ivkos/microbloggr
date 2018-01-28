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
import com.ivkos.microbloggr.follow.services.FollowSuggestionService;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserIdentityResolverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class FollowController
{
    private final UserIdentityResolverService userIdentityResolverService;
    private final FollowService followService;
    private final FollowSuggestionService followSuggestionService;

    @Autowired
    FollowController(UserIdentityResolverService userIdentityResolverService,
                     FollowService followService,
                     FollowSuggestionService followSuggestionService)
    {
        this.userIdentityResolverService = userIdentityResolverService;
        this.followService = followService;
        this.followSuggestionService = followSuggestionService;
    }

    @GetMapping("/users/{identity}/followers")
    List<User> getFollowersOfUser(@PathVariable String identity, @AuthenticationPrincipal User currentUser)
    {
        User user = userIdentityResolverService.resolve(identity, currentUser);
        return followService.getFollowersOfUser(user);
    }

    @GetMapping("/users/{identity}/followees")
    List<User> getFolloweesOfUser(@PathVariable String identity, @AuthenticationPrincipal User currentUser)
    {
        User user = userIdentityResolverService.resolve(identity, currentUser);
        return followService.getFolloweesOfUser(user);
    }

    @PutMapping("/users/{identity}/followers")
    ResponseEntity followUser(@PathVariable String identity, @AuthenticationPrincipal User currentUser)
    {
        User followee = userIdentityResolverService.resolve(identity, currentUser);
        followService.create(currentUser, followee);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{identity}/followers")
    ResponseEntity unfollowUser(@PathVariable String identity, @AuthenticationPrincipal User currentUser)
    {
        User followee = userIdentityResolverService.resolve(identity, currentUser);
        followService.unfollow(currentUser, followee);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/suggest/followees")
    List<User> suggestFollowees(@AuthenticationPrincipal User viewer)
    {
        return followSuggestionService.suggestFollowees(viewer);
    }
}
