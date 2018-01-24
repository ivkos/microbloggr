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

package com.ivkos.microbloggr.user.controllers;

import com.ivkos.microbloggr.api.support.Endpoint;
import com.ivkos.microbloggr.support.forms.request.CheckEmailRequestForm;
import com.ivkos.microbloggr.support.forms.request.CheckVanityRequestForm;
import com.ivkos.microbloggr.support.forms.request.RegistrationRequestForm;
import com.ivkos.microbloggr.support.forms.response.AvailabilityResponse;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.models.UserSession;
import com.ivkos.microbloggr.user.services.UserService;
import com.ivkos.microbloggr.user.services.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
class AuthenticationController
{
    private final UserService userService;
    private final UserSessionService userSessionService;

    @Autowired
    AuthenticationController(UserService userService, UserSessionService userSessionService)
    {
        this.userService = userService;
        this.userSessionService = userSessionService;
    }

    @PostMapping(Endpoint.REGISTER)
    UserSession register(@Valid @RequestBody RegistrationRequestForm form)
    {
        User user = userService.create(form.email, form.password, form.vanity, form.name);
        return userSessionService.createSessionForUser(user);
    }

    @PostMapping(Endpoint.CHECK_EMAIL)
    AvailabilityResponse checkEmail(@Valid @RequestBody CheckEmailRequestForm form)
    {
        boolean available = !userService.isEmailRegistered(form.email);
        return new AvailabilityResponse(available);
    }

    @PostMapping(Endpoint.CHECK_VANITY)
    AvailabilityResponse checkVanity(@Valid @RequestBody CheckVanityRequestForm form)
    {
        boolean available = !userService.isVanityRegistered(form.vanity);
        return new AvailabilityResponse(available);
    }
}
