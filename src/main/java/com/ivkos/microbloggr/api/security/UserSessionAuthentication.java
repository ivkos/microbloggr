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

package com.ivkos.microbloggr.api.security;

import com.ivkos.microbloggr.user.models.UserSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;

class UserSessionAuthentication implements Authentication
{
    private final UserSession session;

    UserSessionAuthentication(UserSession session)
    {
        this.session = session;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return session.getUser().getAuthorities();
    }

    @Override
    public Object getCredentials()
    {
        return session.getUser().getPassword();
    }

    @Override
    public Object getDetails()
    {
        return null;
    }

    @Override
    public Object getPrincipal()
    {
        return session.getUser();
    }

    @Override
    public boolean isAuthenticated()
    {
        return !session.isExpired();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException
    {
        if (!isAuthenticated)
            session.setExpiresAt(Instant.now().minusSeconds(1));
    }

    @Override
    public String getName()
    {
        return session.getUser().getEmail();
    }
}
