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

import com.ivkos.microbloggr.api.support.Endpoint;
import com.ivkos.microbloggr.user.services.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.http.HttpMethod.POST;

@Component
class LoginAuthenticationFilterFactory
{
    private final UserSessionService userSessionService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final MappingJackson2HttpMessageConverter jacksonConverter;

    @Autowired
    LoginAuthenticationFilterFactory(UserSessionService userSessionService,
                                     HandlerExceptionResolver handlerExceptionResolver,
                                     MappingJackson2HttpMessageConverter jacksonConverter)
    {
        this.userSessionService = userSessionService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jacksonConverter = jacksonConverter;
    }

    LoginAuthenticationFilter create(AuthenticationManager authenticationManager)
    {
        return new LoginAuthenticationFilter(
            new AntPathRequestMatcher(Endpoint.LOGIN, POST.name()),
            userSessionService,
            handlerExceptionResolver,
            jacksonConverter.getObjectMapper(),
            authenticationManager
        );
    }
}
