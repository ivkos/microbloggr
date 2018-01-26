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

package com.ivkos.microbloggr.follow.services.impl;

import com.ivkos.microbloggr.follow.models.Follow;
import com.ivkos.microbloggr.follow.models.FollowId;
import com.ivkos.microbloggr.follow.repositories.FollowRepository;
import com.ivkos.microbloggr.follow.services.FollowService;
import com.ivkos.microbloggr.user.models.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class FollowServiceImpl implements FollowService
{
    private final FollowRepository repo;

    FollowServiceImpl(FollowRepository repo)
    {
        this.repo = repo;
    }

    @Override
    public Optional<Follow> find(User follower, User followee)
    {
        return Optional.ofNullable(repo.findOne(new FollowId(follower, followee)));
    }

    @Override
    public Follow create(User follower, User followee)
    {
        ensureNotSameUser(follower, followee);

        return find(follower, followee)
            .orElseGet(() -> create(new Follow(follower, followee)));
    }

    @Override
    public void unfollow(User follower, User followee)
    {
        ensureNotSameUser(follower, followee);

        if (!doesFollow(follower, followee)) return;
        delete(new FollowId(follower, followee));
    }

    @Override
    public boolean doesFollow(User follower, User followee)
    {
        return repo.exists(new FollowId(follower, followee));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFollowersOfUser(User user)
    {
        return repo.findByIdFolloweeOrderByCreatedAtDesc(user)
            .map(Follow::getFollower)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFolloweesOfUser(User user)
    {
        return repo.findByIdFollowerOrderByCreatedAtDesc(user)
            .map(Follow::getFollowee)
            .collect(Collectors.toList());
    }

    @Override
    public long getFollowersCountOfUser(User user)
    {
        return repo.countByIdFollowee(user);
    }

    @Override
    public long getFolloweesCountOfUser(User user)
    {
        return repo.countByIdFollower(user);
    }

    @Override
    public Follow create(Follow entity)
    {
        return repo.save(entity);
    }

    @Override
    public void delete(Follow entity)
    {
        delete(entity.getId());
    }

    @Override
    public void delete(FollowId followId) throws EntityNotFoundException
    {
        if (!repo.exists(followId)) throw new EntityNotFoundException("Follow not found");
        repo.delete(followId);
    }

    @Override
    public Follow findById(FollowId followId) throws EntityNotFoundException
    {
        Follow follow = repo.findOne(followId);
        if (follow == null) throw new EntityNotFoundException("Follow not found");

        return follow;
    }

    @Override
    public List<Follow> findAll()
    {
        return repo.findAll();
    }

    private static void ensureNotSameUser(User follower, User followee)
    {
        if (follower.getId().equals(followee.getId())) {
            throw new IllegalArgumentException("Users may not follow themselves");
        }
    }
}
