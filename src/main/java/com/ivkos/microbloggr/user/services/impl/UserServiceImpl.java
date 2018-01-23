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

package com.ivkos.microbloggr.user.services.impl;

import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.repositories.UserRepository;
import com.ivkos.microbloggr.user.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
class UserServiceImpl implements UserService
{
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    UserServiceImpl(UserRepository repo, PasswordEncoder passwordEncoder)
    {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(User entity)
    {
        return repo.save(entity);
    }

    @Override
    public User findById(UUID id)
    {
        return Optional.ofNullable(repo.findOne(id)).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public List<User> findAll()
    {
        return repo.findAll();
    }

    @Override
    public User update(User entity)
    {
        findById(entity.getId());
        return repo.save(entity);
    }

    @Override
    public void delete(User entity)
    {
        delete(entity.getId());
    }

    @Override
    public void delete(UUID id)
    {
        if (!repo.exists(id)) throw new EntityNotFoundException("User not found");
        repo.delete(id);
    }

    @Override
    public User create(String email, String password, String vanity)
    {
        return create(email, password, vanity, null);
    }

    @Override
    public User create(String email, String password, String vanity, String name)
    {
        email = email.trim();
        vanity = vanity.trim();

        if (isEmailRegistered(email)) throw new EntityExistsException("Email is already registered");
        if (isVanityRegistered(vanity)) throw new EntityExistsException("Username is already taken");

        return create(new User(
            email,
            passwordEncoder.encode(password),
            vanity,
            name
        ));
    }

    @Override
    public User findByEmail(String email)
    {
        email = email.trim();
        return repo.findByEmailIgnoreCase(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public User findByVanity(String vanity)
    {
        vanity = vanity.trim();
        return repo.findByVanityIgnoreCase(vanity)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public boolean isEmailRegistered(String email)
    {
        email = email.trim();
        return repo.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean isVanityRegistered(String vanity)
    {
        vanity = vanity.trim();

        return vanity.equals("me") || repo.existsByVanityIgnoreCase(vanity);
    }

    @Override
    public void disable(UUID id)
    {
        User user = Optional.ofNullable(repo.findOne(id))
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User disabled = user.setEnabled(false);
        repo.save(disabled);
    }
}
