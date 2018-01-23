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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, mode = AdviceMode.PROXY)
class SecurityConfig extends WebSecurityConfigurerAdapter
{
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final SessionTokenAuthenticationFilter sessionTokenAuthenticationFilter;
    private final LoginAuthenticationFilterFactory loginAuthenticationFilterFactory;

    @Autowired
    SecurityConfig(UserDetailsService userDetailsService,
                   AuthenticationEntryPoint authenticationEntryPoint,
                   SessionTokenAuthenticationFilter sessionTokenAuthenticationFilter,
                   LoginAuthenticationFilterFactory loginAuthenticationFilterFactory)
    {
        super(true);
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.sessionTokenAuthenticationFilter = sessionTokenAuthenticationFilter;
        this.loginAuthenticationFilterFactory = loginAuthenticationFilterFactory;
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
            .antMatchers(GET, Endpoint.PICTURE_BY_ID).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterAt(
                loginAuthenticationFilterFactory.create(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterBefore(sessionTokenAuthenticationFilter, BasicAuthenticationFilter.class)
        ;
    }

    @Bean
    PasswordEncoder providePasswordEncoder()
    {
        return new BCryptPasswordEncoder(7);
    }
}
