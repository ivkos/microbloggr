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

import com.ivkos.microbloggr.follow.services.FollowService;
import com.ivkos.microbloggr.post.models.Post;
import com.ivkos.microbloggr.post.services.FeedService;
import com.ivkos.microbloggr.post.services.PostService;
import com.ivkos.microbloggr.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
class FeedServiceImpl implements FeedService
{
    private final PostService postService;
    private final FollowService followService;

    @Autowired
    FeedServiceImpl(PostService postService, FollowService followService)
    {
        this.postService = postService;
        this.followService = followService;
    }

    @Override
    public List<Post> getFeedForUser(User user)
    {
        List<User> followees = followService.getFolloweesOfUser(user);

        if (followees.isEmpty()) return Collections.emptyList();

        return postService.getPostsByMultipleUsers(followees);
    }
}
