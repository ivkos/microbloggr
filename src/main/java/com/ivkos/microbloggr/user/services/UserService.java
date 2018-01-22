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

package com.ivkos.microbloggr.user.services;

import com.ivkos.microbloggr.support.service.CrudEntityService;
import com.ivkos.microbloggr.user.models.User;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface UserService extends CrudEntityService<User, UUID>
{
    User create(String email, String password, String vanity);

    User create(String email, String password, String vanity, String name);

    User findByEmail(String email) throws EntityNotFoundException;

    User findByVanity(String vanity) throws EntityNotFoundException;

    boolean isEmailRegistered(String email);

    boolean isVanityRegistered(String vanity);

    void disable(UUID id) throws EntityNotFoundException;
}
