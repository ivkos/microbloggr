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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest
{
    @Autowired
    private UserService userService;

    @Test
    public void thatFindAllReturnsTheUsers()
    {
        userService.create("me@ivkos.com", "password", "ivkos", "Ivaylo Stoyanov");
        userService.create("ivaylo@ivkos.com", "password", "ivaylo");

        List<User> list = userService.findAll();
        assertThat(list.size(), is(greaterThanOrEqualTo(2)));
    }

    @Test(expected = EntityExistsException.class)
    public void thatItFailsForExistingEmail()
    {
        userService.create("me@ivkos.com", "password", "ivkos");
        userService.create("me@ivkos.com", "password", "ivkos2");
    }

    @Test(expected = EntityExistsException.class)
    public void thatItFailsForExistingVanity()
    {
        userService.create("me@ivkos.com", "password", "ivkos");
        userService.create("me2@ivkos.com", "password", "ivkos");
    }

    @Test
    public void thatUserIsDiscoverableByEmail()
    {
        String email = "me@ivkos.com";
        userService.create(email, "password", "ivkos");
        assertTrue(userService.isEmailRegistered(email));

        User user = userService.findByEmail(email);
        assertNotNull(user);
    }

    @Test(expected = EntityNotFoundException.class)
    public void thatFindByEmailsThrowsForNonExistentEmail()
    {
        String email = "nonexistent@example.com";

        assertFalse(userService.isEmailRegistered(email));

        userService.findByEmail(email);
    }

    @Test
    public void thatUserIsDiscoverableByVanity()
    {
        String vanity = "ivkos";
        userService.create("me@ivkos.com", "password", vanity);
        assertTrue(userService.isVanityRegistered(vanity));

        User user = userService.findByVanity(vanity);
        assertNotNull(user);
    }

    @Test(expected = EntityNotFoundException.class)
    public void thatFindByEmailsThrowsForNonExistentVanity()
    {
        String vanity = "nonexistent_user";

        assertFalse(userService.isVanityRegistered(vanity));

        userService.findByVanity(vanity);
    }

    @Test
    public void thatUserIsFoundById()
    {
        User user = userService.create("ivaylo@ivkos.com", "password", "ivaylo");
        assertNotNull(userService.findById(user.getId()));
    }

    @Test(expected = EntityNotFoundException.class)
    public void thatUserIsNotFoundForNonExistentId()
    {
        UUID fakeId = UUID.nameUUIDFromBytes(new byte[] { 0 });
        userService.findById(fakeId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void thatUserIsDeleted()
    {
        User user = userService.create("tobedeleted@example.com", "password", "tobedeleted");
        assertNotNull(user);

        UUID userId = user.getId();

        userService.delete(userId);
        userService.findById(userId);
    }

    @Test
    public void thatUserIsDisabled()
    {
        User user = userService.create("tobedeleted@example.com", "password", "tobedeleted");
        assertNotNull(user);

        UUID userId = user.getId();

        assertTrue(user.isEnabled());
        userService.disable(userId);

        User user2 = userService.findById(userId);
        assertFalse(user2.isEnabled());
    }
}
