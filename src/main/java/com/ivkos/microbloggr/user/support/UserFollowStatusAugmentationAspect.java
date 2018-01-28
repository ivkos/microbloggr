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

package com.ivkos.microbloggr.user.support;

import com.ivkos.microbloggr.follow.services.FollowService;
import com.ivkos.microbloggr.user.models.User;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
class UserFollowStatusAugmentationAspect
{
    private final FollowService followService;

    @Autowired
    UserFollowStatusAugmentationAspect(FollowService followService)
    {
        this.followService = followService;
    }

    @AfterReturning(
        value = "execution(com.ivkos.microbloggr.user.models.User *(..))",
        returning = "user"
    )
    public void singleUserAdvice(User user)
    {
        augment(user, getViewer());
    }

    @AfterReturning(
        value = "execution(java.util.List<com.ivkos.microbloggr.user.models.User> *(..))",
        returning = "userList"
    )
    public void userListAdvice(List<User> userList)
    {
        User viewer = getViewer();
        userList.forEach(user -> augment(user, viewer));
    }

    private User getViewer()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? (User) authentication.getPrincipal() : null;
    }

    private void augment(User user, User viewer)
    {
        if (user.getFollowersCount() == null) {
            user.setFollowersCount(followService.getFollowersCountOfUser(user));
        }

        if (user.getFolloweesCount() == null) {
            user.setFolloweesCount(followService.getFolloweesCountOfUser(user));
        }

        if (viewer != null && user.getFollowed() == null) {
            user.setFollowed(followService.doesFollow(viewer, user));
        }
    }
}
