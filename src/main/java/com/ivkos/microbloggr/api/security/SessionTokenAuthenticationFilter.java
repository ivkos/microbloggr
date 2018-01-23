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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
class SessionTokenAuthenticationFilter extends GenericFilterBean
{
    public static final String HEADER_SESSION_ID = "X-Session-Id";

    private final UserSessionService userSessionService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    SessionTokenAuthenticationFilter(UserSessionService userSessionService,
                                     HandlerExceptionResolver handlerExceptionResolver)
    {
        super();
        this.userSessionService = userSessionService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain) throws IOException, ServletException
    {
        try {
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
        } catch (Exception e) {
            handlerExceptionResolver.resolveException((HttpServletRequest) req, (HttpServletResponse) res, null, e);
        }
    }
}
