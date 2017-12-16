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
import com.ivkos.microbloggr.user.services.UserSessionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

class SessionTokenAuthenticationFilter extends GenericFilterBean
{
    public static final String HEADER_SESSION_ID = "X-Session-Id";

    private final UserSessionService userSessionService;

    SessionTokenAuthenticationFilter(RequestMatcher requestMatcher, UserSessionService userSessionService)
    {
        super();
        this.userSessionService = userSessionService;
    }

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        String sessionId = request.getHeader(HEADER_SESSION_ID);

        if (sessionId != null) {
            UUID uuid;
            try {
                uuid = UUID.fromString(sessionId);
            } catch (IllegalArgumentException e) {
                throw new BadCredentialsException("Invalid session id", e);
            }

            UserSession userSession = userSessionService.findById(uuid);
            userSession = userSessionService.updateLastSeen(userSession);

            if (userSession.isExpired()) throw new BadCredentialsException("Session expired");

            SecurityContextHolder.getContext().setAuthentication(new UserSessionAuthentication(userSession));
        }

        chain.doFilter(req, res);
    }
}
