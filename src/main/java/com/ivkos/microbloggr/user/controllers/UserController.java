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

import com.ivkos.microbloggr.picture.models.Picture;
import com.ivkos.microbloggr.picture.services.PictureService;
import com.ivkos.microbloggr.support.Role;
import com.ivkos.microbloggr.support.forms.request.UserUpdateRequestForm;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserIdentityResolverService;
import com.ivkos.microbloggr.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/users")
class UserController
{
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserIdentityResolverService userIdentityResolverService;
    private final PictureService pictureService;

    @Autowired
    UserController(UserService userService,
                   PasswordEncoder passwordEncoder,
                   UserIdentityResolverService userIdentityResolverService,
                   PictureService pictureService)
    {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userIdentityResolverService = userIdentityResolverService;
        this.pictureService = pictureService;
    }

    @GetMapping()
    @Secured(Role.ADMIN)
    List<User> getAll()
    {
        return userService.findAll();
    }

    @GetMapping("/{identity}")
    User findByIdOrVanity(@PathVariable String identity, @AuthenticationPrincipal User viewer)
    {
        return userIdentityResolverService.resolve(identity, viewer);
    }

    @DeleteMapping("/{id}")
    @Secured(Role.ADMIN)
    HttpEntity deleteUserById(@PathVariable UUID id)
    {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me")
    User update(@Valid @RequestBody UserUpdateRequestForm form, @AuthenticationPrincipal User viewer)
    {
        // TODO Move to service

        viewer.setName(form.name);
        if (form.vanity != null) viewer.setVanity(form.vanity);
        if (form.password != null) viewer.setPassword(passwordEncoder.encode(form.password));
        if (form.email != null) viewer.setEmail(form.email);

        if (form.pictureId != null) {
            Picture picture = pictureService.findById(UUID.fromString(form.pictureId));
            viewer.setPicture(picture);
        }

        userService.update(viewer);

        return viewer;
    }
}
