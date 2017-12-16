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
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, mode = AdviceMode.PROXY)
class SecurityConfig extends WebSecurityConfigurerAdapter
{
    private final UserDetailsService userDetailsService;
    private final UserSessionService userSessionService;
    private final MappingJackson2HttpMessageConverter jacksonConverter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    SecurityConfig(UserDetailsService userDetailsService,
                   UserSessionService userSessionService,
                   MappingJackson2HttpMessageConverter jacksonConverter,
                   AuthenticationEntryPoint authenticationEntryPoint,
                   HandlerExceptionResolver handlerExceptionResolver)
    {
        super(true);
        this.userDetailsService = userDetailsService;
        this.userSessionService = userSessionService;
        this.jacksonConverter = jacksonConverter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(this.providePasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.anonymous().principal(User.ANONYMOUS);

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

        http
            .authorizeRequests()
            .antMatchers(POST, Endpoint.LOGIN).permitAll()
            .antMatchers(POST, Endpoint.REGISTER).permitAll()
            .antMatchers(POST, Endpoint.CHECK_EMAIL).permitAll()
            .antMatchers(POST, Endpoint.CHECK_VANITY).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterAt(provideLoginAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(provideSessionTokenAuthFilter(), BasicAuthenticationFilter.class)
        ;
    }

    @Bean
    PasswordEncoder providePasswordEncoder()
    {
        return new BCryptPasswordEncoder(7);
    }

    private LoginAuthenticationFilter provideLoginAuthFilter() throws Exception
    {
        LoginAuthenticationFilter filter = new LoginAuthenticationFilter(
            new AntPathRequestMatcher(Endpoint.LOGIN, POST.name()),
            userSessionService,
            handlerExceptionResolver,
            jacksonConverter.getObjectMapper()
        );

        filter.setAuthenticationManager(authenticationManager());

        return filter;
    }

    private SessionTokenAuthenticationFilter provideSessionTokenAuthFilter()
    {
        return new SessionTokenAuthenticationFilter(
            AnyRequestMatcher.INSTANCE,
            userSessionService
        );
    }
}
