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

package com.ivkos.microbloggr.user.services.impl;

import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.models.UserSession;
import com.ivkos.microbloggr.user.repositories.UserSessionRepository;
import com.ivkos.microbloggr.user.services.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.UUID;

@Service
class UserSessionServiceImpl implements UserSessionService
{
    private final UserSessionRepository repo;

    @Autowired
    UserSessionServiceImpl(UserSessionRepository repo)
    {
        this.repo = repo;
    }

    @Override
    public UserSession createSessionForUser(User user)
    {
        return repo.save(new UserSession(user));
    }

    @Override
    public UserSession findById(UUID id)
    {
        return repo.findOneById(id).orElseThrow(() -> new EntityNotFoundException("Session not found"));
    }

    @Override
    public UserSession updateLastSeen(UserSession session)
    {
        return repo.save(session.setLastSeenAt(Instant.now()));
    }
}
