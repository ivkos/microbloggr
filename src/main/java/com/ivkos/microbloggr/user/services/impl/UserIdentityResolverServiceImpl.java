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

package com.ivkos.microbloggr.user.services.impl;

import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserIdentityResolverService;
import com.ivkos.microbloggr.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class UserIdentityResolverServiceImpl implements UserIdentityResolverService
{
    private final UserService userService;

    @Autowired
    UserIdentityResolverServiceImpl(UserService userService)
    {
        this.userService = userService;
    }

    @Override
    public User resolve(String identity, User viewer)
    {
        if (identity.equals("me")) return viewer;
        return resolve(identity);
    }

    @Override
    public User resolve(String identity)
    {
        try {
            UUID id = UUID.fromString(identity);
            return userService.findById(id);
        } catch (IllegalArgumentException e) {
            return userService.findByVanity(identity);
        }
    }
}
