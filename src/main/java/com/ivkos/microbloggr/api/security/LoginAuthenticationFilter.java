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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivkos.microbloggr.support.forms.request.LoginRequestForm;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.models.UserSession;
import com.ivkos.microbloggr.user.services.UserSessionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter implements
    AuthenticationSuccessHandler, AuthenticationFailureHandler
{
    private final UserSessionService userSessionService;
    private final ObjectMapper mapper;
    private final HandlerExceptionResolver handlerExceptionResolver;

    LoginAuthenticationFilter(RequestMatcher requestMatcher,
                              UserSessionService userSessionService,
                              HandlerExceptionResolver handlerExceptionResolver,
                              ObjectMapper mapper)
    {
        super(requestMatcher);

        this.userSessionService = userSessionService;
        this.mapper = mapper;
        this.handlerExceptionResolver = handlerExceptionResolver;

        this.setAuthenticationSuccessHandler(this);
        this.setAuthenticationFailureHandler(this);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
        IOException
    {
        UsernamePasswordAuthenticationToken token;

        try {
            LoginRequestForm form = mapper.readValue(request.getReader(), LoginRequestForm.class);
            token = new UsernamePasswordAuthenticationToken(form.identity, form.password);
        } catch (JsonParseException | JsonMappingException e) {
            throw new BadCredentialsException("Failed to parse authentication request body", e);
        }

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException
    {
        User user = (User) authentication.getPrincipal();
        UserSession session = userSessionService.createSessionForUser(user);
        session = userSessionService.updateLastSeen(session);
        Authentication auth = new UserSessionAuthentication(session);

        SecurityContextHolder
            .getContext()
            .setAuthentication(auth);

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        mapper.writeValue(response.getWriter(), session);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
    {
        handlerExceptionResolver.resolveException(request, response, null, exception);
    }
}
