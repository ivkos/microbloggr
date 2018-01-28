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

package com.ivkos.microbloggr.follow.services.impl;

import com.ivkos.microbloggr.follow.services.FollowService;
import com.ivkos.microbloggr.follow.services.FollowSuggestionService;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("simple")
class SimpleFollowSuggestionService implements FollowSuggestionService
{
    private final FollowService followService;
    private final UserService userService;

    @Autowired
    SimpleFollowSuggestionService(FollowService followService,
                                  UserService userService)
    {
        this.followService = followService;
        this.userService = userService;
    }

    @Override
    public List<User> suggestFollowees(User viewer)
    {
        return userService.findAll().stream()
            .filter(u -> !u.getId().equals(viewer.getId()))
            .filter(u -> !followService.doesFollow(viewer, u))
            .sorted((u1, u2) -> Long.compare(
                followService.getFollowersCountOfUser(u1),
                followService.getFolloweesCountOfUser(u2)
            ))
            .limit(10)
            .collect(Collectors.toList());
    }
}
