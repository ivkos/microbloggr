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

package com.ivkos.microbloggr.follow.services;

import com.ivkos.microbloggr.follow.models.Follow;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FollowServiceTest
{
    @Autowired private FollowService followService;
    @Autowired private UserService userService;

    private User user1;
    private User user2;

    @Before
    public void setUp() throws Exception
    {
        user1 = userService.createUser("user1@example.com", "password", "user1");
        user2 = userService.createUser("user2@example.com", "password", "user2");
    }

    @Test
    public void thatIsNotFollowed()
    {
        assertFalse(followService.doesFollow(user1, user2));
        assertFalse(followService.find(user1, user2).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatUserCannotFollowThemselves()
    {
        followService.create(user1, user1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatUserCannotUnfollowThemselves()
    {
        followService.unfollow(user1, user1);
    }

    @Test
    public void thatIsFollowed()
    {
        Follow follow = followService.create(user1, user2);
        assertNotNull(follow);

        assertTrue(followService.doesFollow(user1, user2));
        assertTrue(followService.getFolloweesOfUser(user1).contains(user2));
        assertTrue(followService.getFollowersOfUser(user2).contains(user1));

        assertEquals(1, followService.getFolloweesCountOfUser(user1));
        assertEquals(1, followService.getFollowersCountOfUser(user2));
    }

    @Test
    public void thatIsUnfollowed()
    {
        thatIsFollowed();

        followService.unfollow(user1, user2);
        assertFalse(followService.doesFollow(user1, user2));
        assertFalse(followService.getFolloweesOfUser(user1).contains(user2));
        assertFalse(followService.getFollowersOfUser(user2).contains(user1));

        assertEquals(0, followService.getFolloweesCountOfUser(user1));
        assertEquals(0, followService.getFollowersCountOfUser(user2));
    }
}
