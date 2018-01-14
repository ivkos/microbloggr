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

package com.ivkos.microbloggr.user.services;

import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.models.UserSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserSessionServiceTest
{
    @Autowired private UserService userService;
    @Autowired private UserSessionService userSessionService;

    @Test
    public void thatItIsCreated()
    {
        User user = userService.createUser("me@ivkos.com", "password", "ivkos");
        assertNotNull(user);

        UserSession session = userSessionService.createSessionForUser(user);
        assertNotNull(session);
    }

    @Test
    public void thatItIsFoundById()
    {
        User user = userService.createUser("me@ivkos.com", "password", "ivkos");
        assertNotNull(user);

        UserSession session = userSessionService.createSessionForUser(user);
        assertNotNull(session);

        UUID id = session.getId();
        UserSession session2 = userSessionService.findById(id);

        assertNotNull(session2);
    }

    @Test(expected = EntityNotFoundException.class)
    public void thatItIsNotFoundById()
    {
        UUID fakeId = UUID.randomUUID();
        userSessionService.findById(fakeId);
    }

    @Test
    public void thatLastSeenIsUpdated() throws Exception
    {
        User user = userService.createUser("me@ivkos.com", "password", "ivkos");
        assertNotNull(user);

        UserSession session = userSessionService.createSessionForUser(user);
        assertNotNull(session);

        Instant lastSeen1 = session.getLastSeenAt();
        assertNull(lastSeen1);

        UserSession session2 = userSessionService.updateLastSeen(session);
        Instant lastSeen2 = session2.getLastSeenAt();
        assertNotNull(lastSeen2);

        Thread.sleep(5);

        UserSession session3 = userSessionService.updateLastSeen(session2);
        Instant lastSeen3 = session3.getLastSeenAt();
        assertTrue(lastSeen3.isAfter(lastSeen2));
    }
}
