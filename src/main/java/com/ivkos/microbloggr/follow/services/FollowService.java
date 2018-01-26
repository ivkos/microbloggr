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

package com.ivkos.microbloggr.follow.services;

import com.ivkos.microbloggr.follow.models.Follow;
import com.ivkos.microbloggr.follow.models.FollowId;
import com.ivkos.microbloggr.support.service.CreatableEntityService;
import com.ivkos.microbloggr.support.service.DeletableEntityService;
import com.ivkos.microbloggr.support.service.ReadableEntityService;
import com.ivkos.microbloggr.user.models.User;

import java.util.List;
import java.util.Optional;

public interface FollowService extends
    CreatableEntityService<Follow>,
    ReadableEntityService<Follow, FollowId>,
    DeletableEntityService<Follow, FollowId>
{
    Optional<Follow> find(User follower, User followee);

    Follow create(User follower, User followee);

    void unfollow(User follower, User followee);

    boolean doesFollow(User follower, User followee);

    List<User> getFollowersOfUser(User user);

    List<User> getFolloweesOfUser(User user);

    long getFollowersCountOfUser(User user);

    long getFolloweesCountOfUser(User user);
}
