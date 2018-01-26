/*
 * Copyright 2018 Ivaylo Stoyanov <me@ivkos.com>
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

package com.ivkos.microbloggr.post.services.impl;

import com.ivkos.microbloggr.follow.services.FollowService;
import com.ivkos.microbloggr.picture.Picture;
import com.ivkos.microbloggr.picture.PictureService;
import com.ivkos.microbloggr.post.models.Post;
import com.ivkos.microbloggr.post.repositories.PostRepository;
import com.ivkos.microbloggr.post.services.PostService;
import com.ivkos.microbloggr.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
class PostServiceImpl implements PostService
{
    private final PostRepository repository;
    private final FollowService followService;
    private final PictureService pictureService;

    @Autowired
    PostServiceImpl(PostRepository repository,
                    FollowService followService,
                    PictureService pictureService)
    {
        this.repository = repository;
        this.followService = followService;
        this.pictureService = pictureService;
    }

    @Override
    public Post findById(UUID id)
    {
        return Optional.ofNullable(repository.findOne(id))
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    @Override
    public List<Post> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Post create(User author, String content, UUID pictureId)
    {
        Picture picture = pictureId != null ? pictureService.findById(pictureId) : null;

        Post post = new Post(author, content, picture);

        return repository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsByUser(User user)
    {
        return repository.findAllByAuthorOrderByCreatedAtDesc(user)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsByMultipleUsers(Collection<User> users)
    {
        return repository.findAllByAuthorInOrderByCreatedAtDesc(users)
            .collect(Collectors.toList());
    }

    @Override
    public List<Post> getFeedForUser(User user)
    {
        List<User> users = new LinkedList<>();

        users.add(user);
        users.addAll(followService.getFolloweesOfUser(user));

        return getPostsByMultipleUsers(users);
    }

    @Override
    public Post create(Post post)
    {
        return repository.save(post);
    }

    @Override
    public void delete(Post post)
    {
        // TODO Refactor
        Post thePost = repository.findOne(post.getId());

        if (thePost == null) return;

        repository.delete(post);
    }

    @Override
    public void delete(UUID uuid) throws EntityNotFoundException
    {
        if (!repository.exists(uuid)) throw new EntityNotFoundException("Post not found");
        repository.delete(uuid);
    }

    @Override
    public Post update(Post entity)
    {
        findById(entity.getId());
        return repository.save(entity);
    }
}
